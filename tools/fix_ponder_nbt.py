"""Drop block entity fields from ponder structures that 26.2 / Create Fly can no longer decode.

Ponder structures are read, and written back out, through the current block entity code; a field
in an old shape logs "Serialization errors" and, for some block entities, can crash (see
reference/create-connected-fly/PORTING.md on the deployer's Owner). Removing the field falls back
to the block entity's default, which is what these fields held anyway:

- create:belt   "Casing": "NONE"      (Create Fly's enum has no NONE; absent means no casing)
- create:funnel "Filter": empty stack (26.2 item codecs reject minecraft:air)

    python tools/fix_ponder_nbt.py            # dry run
    python tools/fix_ponder_nbt.py --write
"""
import gzip
import io
import struct
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PONDER = ROOT / "src/main/resources/assets/create_new_age/ponder"

END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY, LONG_ARRAY = range(13)


def read_payload(f, t):
    if t == BYTE: return struct.unpack(">b", f.read(1))[0]
    if t == SHORT: return struct.unpack(">h", f.read(2))[0]
    if t == INT: return struct.unpack(">i", f.read(4))[0]
    if t == LONG: return struct.unpack(">q", f.read(8))[0]
    if t == FLOAT: return struct.unpack(">f", f.read(4))[0]
    if t == DOUBLE: return struct.unpack(">d", f.read(8))[0]
    if t == BYTE_ARRAY:
        n = struct.unpack(">i", f.read(4))[0]
        return f.read(n)
    if t == STRING:
        n = struct.unpack(">H", f.read(2))[0]
        return f.read(n).decode("utf-8")
    if t == LIST:
        et = f.read(1)[0]
        n = struct.unpack(">i", f.read(4))[0]
        return (et, [read_payload(f, et) for _ in range(n)])
    if t == COMPOUND:
        out = {}
        while True:
            ct = f.read(1)[0]
            if ct == END:
                return out
            name = read_payload(f, STRING)
            out[name] = (ct, read_payload(f, ct))
    if t == INT_ARRAY:
        n = struct.unpack(">i", f.read(4))[0]
        return list(struct.unpack(f">{n}i", f.read(4 * n)))
    if t == LONG_ARRAY:
        n = struct.unpack(">i", f.read(4))[0]
        return list(struct.unpack(f">{n}q", f.read(8 * n)))
    raise ValueError(f"unknown tag {t}")


def write_payload(f, t, v):
    if t == BYTE: f.write(struct.pack(">b", v))
    elif t == SHORT: f.write(struct.pack(">h", v))
    elif t == INT: f.write(struct.pack(">i", v))
    elif t == LONG: f.write(struct.pack(">q", v))
    elif t == FLOAT: f.write(struct.pack(">f", v))
    elif t == DOUBLE: f.write(struct.pack(">d", v))
    elif t == BYTE_ARRAY: f.write(struct.pack(">i", len(v)) + v)
    elif t == STRING:
        b = v.encode("utf-8")
        f.write(struct.pack(">H", len(b)) + b)
    elif t == LIST:
        et, items = v
        f.write(bytes([et]) + struct.pack(">i", len(items)))
        for item in items:
            write_payload(f, et, item)
    elif t == COMPOUND:
        for name, (ct, cv) in v.items():
            f.write(bytes([ct]))
            write_payload(f, STRING, name)
            write_payload(f, ct, cv)
        f.write(bytes([END]))
    elif t == INT_ARRAY: f.write(struct.pack(">i", len(v)) + struct.pack(f">{len(v)}i", *v))
    elif t == LONG_ARRAY: f.write(struct.pack(">i", len(v)) + struct.pack(f">{len(v)}q", *v))


def value(compound, key):
    return compound[key][1] if key in compound else None


def is_empty_stack(tag):
    return isinstance(tag, dict) and value(tag, "id") == "minecraft:air"


def fix(root):
    changes = []
    for block in value(root, "blocks")[1]:
        nbt = value(block, "nbt")
        if not nbt:
            continue
        kind = value(nbt, "id")
        if kind == "create:belt" and value(nbt, "Casing") == "NONE":
            del nbt["Casing"]
            changes.append("belt Casing")
        if kind == "create:funnel" and is_empty_stack(value(nbt, "Filter")):
            del nbt["Filter"]
            changes.append("funnel Filter")
    return changes


for path in sorted(PONDER.glob("*.nbt")):
    f = io.BytesIO(gzip.decompress(path.read_bytes()))
    root_type = f.read(1)[0]
    root_name = read_payload(f, STRING)
    root = read_payload(f, root_type)
    changes = fix(root)
    if not changes:
        continue
    print(f"{path.name}: {', '.join(changes)}")
    if "--write" in sys.argv:
        out = io.BytesIO()
        out.write(bytes([root_type]))
        write_payload(out, STRING, root_name)
        write_payload(out, root_type, root)
        path.write_bytes(gzip.compress(out.getvalue()))
