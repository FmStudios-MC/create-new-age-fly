"""Write the 26.2 item model definitions (assets/create_new_age/items/<id>.json).

26.2 no longer renders an item from models/item/<id>.json alone; every item needs a definition
file. Plain items point at their existing item model. The items that upstream drew with
ItemShaftRenderer / CarbonBrushesItemRenderer (a Create shaft, and for the brushes the coil, added
on top of the item model) become vanilla composite models, with the extra parts transformed
exactly as those renderers did: rotate 90 degrees about X, then 1 radian about Y, then translate
by the offset in pixels. The transform is about the item's centre, as the renderers' was.

    python tools/gen_item_definitions.py        # ids are read from CNABlocks / CNAItems
"""
import json
import math
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
JAVA = ROOT / "src/main/java/org/antarcticgardens/cna"
OUT = ROOT / "src/main/resources/assets/create_new_age/items"

# id -> (offset in pixels along x, extra part models)
SHAFT = "create:block/shaft"
COIL = "create_new_age:block/carbon_brushes/coil"
WITH_SHAFT = {
    "basic_motor": (0.0, [SHAFT]),
    "advanced_motor": (0.0, [SHAFT]),
    "reinforced_motor": (0.0, [SHAFT]),
    "stirling_engine": (0.5, [SHAFT]),
    "basic_energiser": (0.5, [SHAFT]),
    "advanced_energiser": (0.5, [SHAFT]),
    "reinforced_energiser": (0.5, [SHAFT]),
    "carbon_brushes": (0.0, [SHAFT, COIL]),
}


def shaft_transformation(offset_px):
    ax, ay = math.radians(90), 1.0  # Axis.XP.rotationDegrees(90), then Axis.YP.rotation(1.0f)
    sx, cx = math.sin(ax / 2), math.cos(ax / 2)
    sy, cy = math.sin(ay / 2), math.cos(ay / 2)
    q = [sx * cy, cx * sy, sx * sy, cx * cy]  # qx(90) * qy(1), as x, y, z, w
    t = offset_px / 16
    translation = [t * math.cos(ay), t * math.sin(ay), 0.0]  # Rx(90) * Ry(1) * (t, 0, 0)
    return {"translation": [round(c, 6) for c in translation],
            "left_rotation": [round(c, 6) for c in q],
            "scale": [1, 1, 1],
            "right_rotation": [0, 0, 0, 1]}


def ids():
    names = []
    for source in ("CNABlocks.java", "CNAItems.java"):
        text = (JAVA / source).read_text(encoding="utf-8")
        names += re.findall(r'\bregister(?:Hidden)?\("([a-z_]+)"', text)
    return names


def model(ref):
    return {"type": "minecraft:model", "model": ref}


OUT.mkdir(parents=True, exist_ok=True)
for item_id in ids():
    base = model(f"create_new_age:item/{item_id}")
    if item_id in WITH_SHAFT:
        offset, parts = WITH_SHAFT[item_id]
        definition = {"model": {"type": "minecraft:composite", "models": [
            base,
            {"type": "minecraft:composite", "transformation": shaft_transformation(offset),
             "models": [model(p) for p in parts]},
        ]}}
    else:
        definition = {"model": base}
    (OUT / f"{item_id}.json").write_text(json.dumps(definition, indent=2) + "\n", encoding="utf-8")
print(f"{len(ids())} item definitions in {OUT.relative_to(ROOT)}")
