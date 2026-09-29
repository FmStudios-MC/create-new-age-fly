"""Write the 26.2 item model definitions (assets/create_new_age/items/<id>.json).

26.2 no longer renders an item from models/item/<id>.json alone; every item needs a definition
file. Plain items point at their existing item model. The items that upstream drew with
ItemShaftRenderer / CarbonBrushesItemRenderer (a Create shaft, and for the brushes the coil, added
on top of the item model) become vanilla composite models, with the extra parts transformed
exactly as those renderers did: rotate about X, then about Y, then translate by the offset in
pixels, about the item's centre as the renderers did (the composite model's
transformation turns about the corner, so the centre is folded into the translation).

    python tools/gen_item_definitions.py        # ids are read from CNABlocks / CNAItems
"""
import json
import math
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
JAVA = ROOT / "src/main/java/org/antarcticgardens/cna"
OUT = ROOT / "src/main/resources/assets/create_new_age/items"

# id -> parts, each (extra part model, offset in pixels along x, X rotation in degrees, Y rotation in radians)
SHAFT = "create:block/shaft"
COIL = "create_new_age:block/carbon_brushes/coil"
MOTOR = [(SHAFT, 0.0, 90, 1.0)]
OFFSET_SHAFT = [(SHAFT, 0.5, 90, 1.0)]
WITH_SHAFT = {
    "basic_motor": MOTOR,
    "advanced_motor": MOTOR,
    "reinforced_motor": MOTOR,
    "stirling_engine": OFFSET_SHAFT,
    "basic_energiser": OFFSET_SHAFT,
    "advanced_energiser": OFFSET_SHAFT,
    "reinforced_energiser": OFFSET_SHAFT,
    # upstream's brushes renderer has no X rotation, and it turns the pose again for the coil
    # without popping it, so the coil ends up turned twice
    "carbon_brushes": [(SHAFT, 0.0, 0, 1.0), (COIL, 0.0, 0, 2.0)],
}


def shaft_transformation(offset_px, ax_deg, ay):
    ax = math.radians(ax_deg)  # Axis.XP.rotationDegrees(ax_deg), then Axis.YP.rotation(ay)
    sx, cx = math.sin(ax / 2), math.cos(ax / 2)
    sy, cy = math.sin(ay / 2), math.cos(ay / 2)
    q = [sx * cy, cx * sy, sx * sy, cx * cy]  # qx(90) * qy(1), as x, y, z, w
    # The renderer turned the shaft about the item's centre; the composite model's transformation
    # is applied after ItemTransform.apply's final translate(-0.5), so it turns about the model's
    # corner. So: centre + R * (offset - centre).
    def rotate(v):
        x, y, z = v
        x, z = x * math.cos(ay) + z * math.sin(ay), -x * math.sin(ay) + z * math.cos(ay)  # Ry(1)
        y, z = y * math.cos(ax) - z * math.sin(ax), y * math.sin(ax) + z * math.cos(ax)  # then Rx(90)
        return [x, y, z]
    c = 0.5
    r = rotate([offset_px / 16 - c, -c, -c])
    translation = [c + r[0], c + r[1], c + r[2]]
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
        definition = {"model": {"type": "minecraft:composite", "models": [base] + [
            {"type": "minecraft:composite", "transformation": shaft_transformation(offset, ax, ay),
             "models": [model(part)]}
            for part, offset, ax, ay in WITH_SHAFT[item_id]]}}
    else:
        definition = {"model": base}
    (OUT / f"{item_id}.json").write_text(json.dumps(definition, indent=2) + "\n", encoding="utf-8")
print(f"{len(ids())} item definitions in {OUT.relative_to(ROOT)}")
