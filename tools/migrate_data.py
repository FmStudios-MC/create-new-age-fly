"""Rewrite CNA's 1.21.1 (NeoForge) recipe JSONs into the 26.2 / Create Fly shapes.

Every target shape was read off a real file: vanilla's from the 26.2 jar, Create's from Create
Fly's own data/create/recipe/<type>/. Only files it actually changes are written, and it prints
what it would change first:

    python tools/migrate_data.py            # dry run
    python tools/migrate_data.py --write

What it does:
- ingredients: {"item": x} -> "x", {"tag": t} -> "#t", lists of those -> lists of strings
- single-input processing recipes (crushing, cutting, pressing, milling, energising, ...):
  "ingredients": [x] -> "ingredient": x
- deploying / item_application: "ingredients": [target, applied] -> "target", "ingredient"
- mixing / compacting: NeoForge fluid stacks move to "fluid_ingredients" as Create Fly's
  {"type": "fluid_stack", ...}, amounts x81 (Fabric droplets per mB)
- sequenced_assembly: the weighted "results" list becomes "result" (with its share of the total
  weight as chance) plus "junks"; each step names the transitional item as "$ingredient" /
  "$result"
- create:cutting without "processing_time" gets 50 (the saw's own default)
- neoforge:conditions -> fabric:load_conditions; neoforge:never -> not(true)
"""
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DATA_ROOTS = [ROOT / "src/generated/resources/data", ROOT / "src/main/resources/resourcepacks"]
DROPLETS_PER_MB = 81

SINGLE_INPUT = {"create:crushing", "create:cutting", "create:pressing", "create:milling",
                "create:splashing", "create:haunting", "create:sandpaper_polishing",
                "create_new_age:energising"}
TWO_INPUT = {"create:deploying", "create:item_application"}
BASIN = {"create:mixing", "create:compacting"}


def ingredient(value):
    if isinstance(value, str):
        return value
    if isinstance(value, list):
        return [ingredient(v) for v in value]
    if isinstance(value, dict):
        if set(value) == {"item"}:
            return value["item"]
        if set(value) == {"tag"}:
            return "#" + value["tag"]
    raise ValueError(f"unsupported ingredient {value!r}")


def is_fluid(value):
    return isinstance(value, dict) and "fluid" in value


def fluid(value):
    return {"type": "fluid_stack", "amount": value["amount"] * DROPLETS_PER_MB, "fluid": value["fluid"]}


def condition(value):
    kind = value.get("type")
    if kind == "neoforge:never":
        return {"condition": "fabric:not", "value": {"condition": "fabric:true"}}
    if kind == "neoforge:mod_loaded":
        return {"condition": "fabric:all_mods_loaded", "values": [value["modid"]]}
    if kind == "neoforge:not":
        return {"condition": "fabric:not", "value": condition(value["value"])}
    raise ValueError(f"unsupported condition {value!r}")


def recipe(obj, transitional=None):
    kind = obj.get("type")
    out = dict(obj)

    if kind in SINGLE_INPUT and "ingredients" in out:
        (only,) = out.pop("ingredients")
        out["ingredient"] = ingredient(only)
    elif kind in TWO_INPUT and "ingredients" in out:
        target, applied = out.pop("ingredients")
        out["target"] = ingredient(target)
        out["ingredient"] = ingredient(applied)
    elif kind in BASIN and "ingredients" in out:
        items = [i for i in out["ingredients"] if not is_fluid(i)]
        fluids = [i for i in out["ingredients"] if is_fluid(i)]
        out["ingredients"] = [ingredient(i) for i in items]
        if fluids:
            out["fluid_ingredients"] = [fluid(f) for f in fluids]
    elif kind in ("minecraft:crafting_shaped", "create:mechanical_crafting"):
        out["key"] = {k: ingredient(v) for k, v in out["key"].items()}
    elif kind == "minecraft:crafting_shapeless":
        out["ingredients"] = [ingredient(i) for i in out["ingredients"]]
    elif kind == "create:sequenced_assembly":
        out["ingredient"] = ingredient(out["ingredient"])
        transitional = out["transitional_item"]["id"]
        if "results" in out:  # not yet migrated
            results = out.pop("results")
            total = sum(r.get("chance", 1.0) for r in results)
            first = dict(results[0])
            weight = first.pop("chance", 1.0)
            if weight != total:
                first["chance"] = weight / total
            out["result"] = first
            if len(results) > 1:
                out["junks"] = results[1:]
        out["sequence"] = [recipe(step, transitional) for step in out["sequence"]]
        # keep Create Fly's key order: ingredient, transitional_item, result, junks, loops, sequence
        order = ["type", "ingredient", "transitional_item", "result", "junks", "loops", "sequence"]
        out = {k: out[k] for k in order if k in out} | {k: v for k, v in out.items() if k not in order}

    if kind == "create:cutting" and "processing_time" not in out:
        # Required by Create Fly's codec. Upstream left it out, which old Create read as 0; 50 is
        # what the saw falls back to for a recipe without a time, and avoids a zero duration.
        out["processing_time"] = 50

    if transitional is not None:
        for key in ("ingredient", "target"):
            if out.get(key) == transitional:
                out[key] = "$ingredient"
        if "results" in out:
            out["results"] = ["$result" if r == {"id": transitional} else r for r in out["results"]]

    if "neoforge:conditions" in out:
        out["fabric:load_conditions"] = [condition(c) for c in out.pop("neoforge:conditions")]
    return out


changed = 0
for data_root in DATA_ROOTS:
    for path in sorted(data_root.rglob("recipe/**/*.json")):
        original = json.loads(path.read_text(encoding="utf-8"))
        try:
            migrated = recipe(original)
        except ValueError as e:
            print(f"SKIP {path.relative_to(ROOT)}: {e}")
            continue
        if migrated != original:
            changed += 1
            print(f"{'WRITE' if '--write' in sys.argv else 'WOULD'} {path.relative_to(ROOT)}")
            if "--write" in sys.argv:
                path.write_text(json.dumps(migrated, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
print(f"\n{changed} files {'rewritten' if '--write' in sys.argv else 'to rewrite'}")
