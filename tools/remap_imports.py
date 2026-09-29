"""Rewrite Create / Catnip / Ponder / Flywheel imports to their Create Fly locations.

For each import, the first segment starting with a capital letter is the class. It is looked up
by simple name in the Create Fly class list (tools/createfly-classes.txt, taken from the jar we
compile against). Several candidates are ranked by how many trailing package segments they share
with the original. Anything without a candidate is reported and left untouched.

    python tools/remap_imports.py            # dry run, prints the mapping and unmatched imports
    python tools/remap_imports.py --write
"""
import re
import sys
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "src" / "main" / "java"
PREFIXES = ("com.simibubi.create.", "net.createmod.catnip.", "net.createmod.ponder.", "dev.engine_room.flywheel.")
REF = re.compile(r"\b((?:com\.simibubi\.create|net\.createmod\.catnip|net\.createmod\.ponder|dev\.engine_room\.flywheel)(?:\.[A-Za-z_][A-Za-z0-9_]*)+)")

by_name = defaultdict(list)
for line in (ROOT / "tools" / "createfly-classes.txt").read_text().split():
    by_name[line.rsplit(".", 1)[-1]].append(line)


def split_class(ref):
    """Return (class_fqn, rest) where rest is the nested/member suffix after the top-level class."""
    parts = ref.split(".")
    for i, p in enumerate(parts):
        if p[:1].isupper():
            return ".".join(parts[: i + 1]), ".".join(parts[i + 1:])
    return None, None


def pick(old_fqn):
    name = old_fqn.rsplit(".", 1)[-1]
    cands = by_name.get(name, [])
    if not cands:
        return None
    old_pkg = old_fqn.split(".")[:-1]

    def score(c):
        pkg = c.split(".")[:-1]
        s = 0
        for a, b in zip(reversed(old_pkg), reversed(pkg)):
            if a != b:
                break
            s += 1
        # prefer a common-side class over a client one on a tie; the mod has one source set anyway
        return (s, ".client." not in c)

    return max(cands, key=score)


mapping, unmatched = {}, defaultdict(set)
files = sorted(SRC.rglob("*.java"))
for f in files:
    for ref in REF.findall(f.read_text(encoding="utf-8")):
        cls, _ = split_class(ref)
        if cls is None or cls in mapping:
            continue
        new = pick(cls)
        if new:
            mapping[cls] = new
        else:
            unmatched[cls].add(f.relative_to(SRC).as_posix())

for old, new in sorted(mapping.items()):
    print(f"MAP  {old} -> {new}")
for old, fs in sorted(unmatched.items()):
    print(f"MISS {old}  ({len(fs)} files)")
print(f"\n{len(mapping)} mapped, {len(unmatched)} unmatched")

if "--write" in sys.argv:
    changed = 0
    for f in files:
        text = f.read_text(encoding="utf-8")

        def sub(m):
            cls, rest = split_class(m.group(1))
            if cls in mapping:
                return mapping[cls] + ("." + rest if rest else "")
            return m.group(1)

        new_text = REF.sub(sub, text)
        if new_text != text:
            f.write_text(new_text, encoding="utf-8")
            changed += 1
    print(f"rewrote {changed} files")
