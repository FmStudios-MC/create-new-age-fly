"""Split omnidirectional connected-texture sheets into Create Fly's per-tile sprites.

Create reads one 8x8 sheet, `block/<name>_connected.png`, and picks a tile by position. Create Fly
reads one sprite per tile, `block/<name>_connected/<i>.png`, numbered by its
OmnidirectionalCTType.MAP (1..46; 0 is the original texture). This maps each Create Fly index back
to the sheet position Create's OMNIDIRECTIONAL.getTextureIndex computes for the same connections.

    python tools/split_ct_sheets.py <sheet.png> [<sheet.png> ...]          # writes next to each sheet
    python tools/split_ct_sheets.py --verify <create sheet> <create fly tile dir>
"""
import sys
from pathlib import Path

from PIL import Image

UP, DOWN, LEFT, RIGHT = 1, 2, 4, 8
TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT = 16, 32, 64, 128
UDL, UDR, ULR, DLR = UP | DOWN | LEFT, UP | DOWN | RIGHT, UP | LEFT | RIGHT, DOWN | LEFT | RIGHT
UDLR = UP | DOWN | LEFT | RIGHT
TLR, BLR = TOP_LEFT | TOP_RIGHT, BOTTOM_LEFT | BOTTOM_RIGHT

# Create Fly's OmnidirectionalCTType.MAP, in index order (index = position + 1)
FLY_ORDER = [
    UP, DOWN, UP | DOWN, LEFT, UP | LEFT, DOWN | LEFT, UDL, UP | LEFT | TOP_LEFT, DOWN | LEFT | BOTTOM_LEFT,
    RIGHT, UP | RIGHT, DOWN | RIGHT, UDR, UP | RIGHT | TOP_RIGHT, DOWN | RIGHT | BOTTOM_RIGHT,
    LEFT | RIGHT, ULR, DLR, UDLR, UDLR | TOP_RIGHT, UDLR | TOP_LEFT, UDLR | TLR,
    UDL | BOTTOM_LEFT, UDL | TOP_LEFT, UDL | TOP_LEFT | BOTTOM_LEFT,
    UDLR | BOTTOM_LEFT, UDLR | TOP_RIGHT | BOTTOM_LEFT, UDLR | TOP_LEFT | BOTTOM_LEFT, UDLR | TLR | BOTTOM_LEFT,
    UDR | BOTTOM_RIGHT, UDR | TOP_RIGHT, UDR | TOP_RIGHT | BOTTOM_RIGHT,
    UDLR | BOTTOM_RIGHT, UDLR | TOP_RIGHT | BOTTOM_RIGHT, UDLR | TOP_LEFT | BOTTOM_RIGHT, UDLR | TLR | BOTTOM_RIGHT,
    ULR | TOP_LEFT, ULR | TOP_RIGHT, ULR | TLR,
    UDLR | BLR, UDLR | TOP_RIGHT | BLR, UDLR | TOP_LEFT | BLR, UDLR | TLR | BLR,
    DLR | BOTTOM_LEFT, DLR | BOTTOM_RIGHT, DLR | BLR,
]


def create_sheet_index(mask):
    """Create's AllCTTypes.OMNIDIRECTIONAL.getTextureIndex, transcribed."""
    up, down, left, right = bool(mask & UP), bool(mask & DOWN), bool(mask & LEFT), bool(mask & RIGHT)
    tl, tr, bl, br = bool(mask & TOP_LEFT), bool(mask & TOP_RIGHT), bool(mask & BOTTOM_LEFT), bool(mask & BOTTOM_RIGHT)
    tile_x = tile_y = 0
    borders = (not up) + (not down) + (not left) + (not right)
    if up:
        tile_x += 1
    if down:
        tile_x += 2
    if left:
        tile_y += 1
    if right:
        tile_y += 2
    if borders == 0:
        if tr:
            tile_x += 1
        if tl:
            tile_x += 2
        if br:
            tile_y += 2
        if bl:
            tile_y += 1
    if borders == 1:
        if not right and (tl or bl):
            tile_y, tile_x = 4, -1 + bl + tl * 2
        if not left and (tr or br):
            tile_y, tile_x = 5, -1 + br + tr * 2
        if not down and (tl or tr):
            tile_y, tile_x = 6, -1 + tl + tr * 2
        if not up and (bl or br):
            tile_y, tile_x = 7, -1 + bl + br * 2
    if borders == 2:
        if (up and left and tl) or (down and left and bl) or (up and right and tr) or (down and right and br):
            tile_x += 3
    return tile_x + 8 * tile_y


MAPPING = {i + 1: create_sheet_index(mask) for i, mask in enumerate(FLY_ORDER)}
assert len(set(MAPPING.values())) == len(MAPPING), "two Create Fly tiles map to one sheet tile"
assert all(0 <= v < 64 for v in MAPPING.values())


def tiles(sheet_path):
    sheet = Image.open(sheet_path).convert("RGBA")
    size = sheet.width // 8
    for fly_index, sheet_index in MAPPING.items():
        x, y = sheet_index % 8, sheet_index // 8
        yield fly_index, sheet.crop((x * size, y * size, (x + 1) * size, (y + 1) * size))


if __name__ == "__main__":
    if sys.argv[1] == "--verify":
        sheet, tile_dir = sys.argv[2], Path(sys.argv[3])
        bad = [i for i, tile in tiles(sheet) if list(tile.getdata()) != list(Image.open(tile_dir / f"{i}.png").convert("RGBA").getdata())]
        print("all 46 tiles match" if not bad else f"mismatching tiles: {bad}")
        sys.exit(1 if bad else 0)
    for sheet in sys.argv[1:]:
        out = Path(sheet).with_suffix("")  # <name>_connected.png -> <name>_connected/
        out.mkdir(exist_ok=True)
        for i, tile in tiles(sheet):
            tile.save(out / f"{i}.png")
        print(f"{sheet} -> {out}/1..46.png")
