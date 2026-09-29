"""Print each distinct javac error of a given kind with the source lines around it.

    python tools/show_errors.py <compile log> "<error message substring>" [lines after]
"""
import re
import sys

log, needle = sys.argv[1], sys.argv[2]
after = int(sys.argv[3]) if len(sys.argv) > 3 else 12
seen = set()
for line in open(log, encoding="utf-8", errors="replace"):
    m = re.match(r"(.*\.java):(\d+): error: (.*)", line)
    if not m or needle not in m.group(3):
        continue
    path, num = m.group(1), int(m.group(2))
    if (path, num) in seen:
        continue
    seen.add((path, num))
    src = open(path, encoding="utf-8").read().split("\n")
    short = re.split(r"[\\/]cna[\\/]", path)[-1]
    print(f"=== {short}:{num}  ({m.group(3)})")
    print("\n".join(src[max(0, num - 2):num + after]))
