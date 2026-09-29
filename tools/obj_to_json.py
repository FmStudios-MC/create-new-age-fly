"""Convert a Blockbench OBJ export into a vanilla 26.2 block model.

NeoForge loaded the generator coil through its OBJ loader, which Fabric does not have. 26.2's
block model elements accept a free Euler rotation ("rotation": {"x", "y", "z"}, applied as
Rz * Ry * Rx, see CuboidRotation$EulerXYZRotation), so every rectangular OBJ face becomes one flat
element carrying a single "south" face, rotated into place about the face's centre.

    python tools/obj_to_json.py <model.obj> <texture key> <out.json> [--flip-v] [--template base.json] [--quads]

The template's other keys (parent, textures, display, ambientocclusion, ...) are kept.

Elements cannot shear, so a face that is a parallelogram rather than a rectangle only comes out
approximately. With --quads the faces are written as raw quads for CNA's own
"create_new_age:quads" model type (client/model/QuadListModel), which bakes them exactly; the
generator coil's windings need that.
"""
import json
import math
import sys


def sub(a, b):
    return [a[i] - b[i] for i in range(3)]


def add(a, b):
    return [a[i] + b[i] for i in range(3)]


def scale(a, s):
    return [x * s for x in a]


def dot(a, b):
    return sum(a[i] * b[i] for i in range(3))


def cross(a, b):
    return [a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]]


def length(a):
    return math.sqrt(dot(a, a))


def norm(a):
    n = length(a)
    return [x / n for x in a]


def euler_zyx(u, v, n):
    """Angles (x, y, z) in degrees with Rz(z) * Ry(y) * Rx(x) = [u v n] (columns)."""
    r = [[u[0], v[0], n[0]], [u[1], v[1], n[1]], [u[2], v[2], n[2]]]
    sy = max(-1.0, min(1.0, -r[2][0]))
    y = math.asin(sy)
    if abs(sy) < 0.99999:
        x = math.atan2(r[2][1], r[2][2])
        z = math.atan2(r[1][0], r[0][0])
    else:  # gimbal lock: fold everything into x
        z = 0.0
        x = math.atan2(-r[1][2], r[1][1])
    return [math.degrees(x), math.degrees(y), math.degrees(z)]


def rnd(x):
    return round(x, 4) + 0.0


def convert(obj_path, texture, flip_v):
    verts, uvs, faces = [], [], []
    for line in open(obj_path, encoding="utf-8"):
        p = line.split()
        if not p:
            continue
        if p[0] == "v":
            verts.append([float(c) * 16 for c in p[1:4]])
        elif p[0] == "vt":
            u, v = float(p[1]), float(p[2])
            uvs.append([u * 16, (1 - v if flip_v else v) * 16])
        elif p[0] == "f":
            faces.append([tuple(int(i) - 1 for i in c.split("/")[:2]) for c in p[1:]])

    elements, skipped = [], 0
    for face in faces:
        if len(face) != 4:
            skipped += 1
            continue
        pts = [verts[vi] for vi, _ in face]
        tex = [uvs[ti] for _, ti in face]
        # pick the corner whose first edge runs along the texture's u axis
        best = None
        for k in range(4):
            p0, p1, p3 = pts[k], pts[(k + 1) % 4], pts[(k + 3) % 4]
            t0, t1, t3 = tex[k], tex[(k + 1) % 4], tex[(k + 3) % 4]
            du = abs(t1[0] - t0[0]) + abs(t3[1] - t0[1])
            if best is None or du > best[0]:
                best = (du, p0, p1, p3, t0, t1, t3)
        _, p0, p1, p3, t0, t1, t3 = best
        e1, e2 = sub(p1, p0), sub(p3, p0)
        w, h = length(e1), length(e2)
        if w < 1e-6 or h < 1e-6:
            skipped += 1
            continue
        u_axis, v_axis = norm(e1), norm(e2)
        if abs(dot(u_axis, v_axis)) > 1e-3:
            print(f"warning: face is not rectangular (cos {dot(u_axis, v_axis):.4f})", file=sys.stderr)
            v_axis = norm(sub(v_axis, scale(u_axis, dot(u_axis, v_axis))))
        n_axis = cross(u_axis, v_axis)
        centre = add(p0, add(scale(e1, 0.5), scale(e2, 0.5)))
        frm = [centre[0] - w / 2, centre[1] - h / 2, centre[2]]
        to = [centre[0] + w / 2, centre[1] + h / 2, centre[2]]
        rx, ry, rz = euler_zyx(u_axis, v_axis, n_axis)
        # south face: u grows with +x from the (from.x) edge, v grows downwards from the top (to.y)
        face_uv = [t0[0], t3[1], t1[0], t0[1]]
        elements.append({
            "from": [rnd(c) for c in frm],
            "to": [rnd(c) for c in to],
            "rotation": {"origin": [rnd(c) for c in centre], "x": rnd(rx), "y": rnd(ry), "z": rnd(rz)},
            "faces": {"south": {"uv": [rnd(c) for c in face_uv], "texture": texture}},
        })
    out_of_range = [e for e in elements if any(c < -16 or c > 32 for c in e["from"] + e["to"])]
    return elements, skipped, out_of_range


if __name__ == "__main__":
    obj_path, texture, out_path = sys.argv[1:4]
    flip_v = "--flip-v" in sys.argv
    template = {}
    if "--template" in sys.argv:
        template = json.load(open(sys.argv[sys.argv.index("--template") + 1], encoding="utf-8"))
    for key in ("loader", "model", "flip_v"):
        template.pop(key, None)
    if "--quads" in sys.argv:
        verts, uvs, quads = [], [], []
        for line in open(obj_path, encoding="utf-8"):
            p = line.split()
            if p and p[0] == "v":
                verts.append([float(c) * 16 for c in p[1:4]])
            elif p and p[0] == "vt":
                uvs.append([float(p[1]) * 16, ((1 - float(p[2])) if flip_v else float(p[2])) * 16])
            elif p and p[0] == "f":
                idx = [tuple(int(i) - 1 for i in c.split("/")[:2]) for c in p[1:]]
                if len(idx) == 3:
                    idx.append(idx[2])  # a triangle as a degenerate quad
                quads.append({"texture": texture,
                              "vertices": [[rnd(c) for c in verts[vi] + uvs[ti]] for vi, ti in idx]})
        template = {"fabric:type": "create_new_age:quads"} | template
        template["quads"] = quads
        json.dump(template, open(out_path, "w", encoding="utf-8"), indent=1)
        print(f"{len(quads)} quads")
        sys.exit()
    elements, skipped, bad = convert(obj_path, texture, flip_v)
    template["elements"] = elements
    json.dump(template, open(out_path, "w", encoding="utf-8"), indent=1)
    print(f"{len(elements)} elements, {skipped} faces skipped, {len(bad)} outside -16..32")
