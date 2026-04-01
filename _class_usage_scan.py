from pathlib import Path
import re
root = Path(r"e:\\SDV_B3\\Java\\Game")
main_files = list((root / "core/src/main/java").rglob("*.java"))
all_files = list((root / "core/src/main/java").rglob("*.java")) + list((root / "core/src/test/java").rglob("*.java")) + list((root / "lwjgl3/src/main/java").rglob("*.java"))
texts = {p: p.read_text(encoding="utf-8") for p in all_files}
for f in sorted(main_files):
    m = re.search(r"\b(class|enum|interface)\s+(\w+)", f.read_text(encoding="utf-8"))
    if not m:
        continue
    name = m.group(2)
    count = 0
    for t in texts.values():
        count += len(re.findall(r"\b" + re.escape(name) + r"\b", t))
    if count <= 1:
        print(f"POSSIBLY_UNUSED_CLASS {name} -> {f}")
