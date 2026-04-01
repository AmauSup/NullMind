from pathlib import Path
import re
root = Path(r"e:\\SDV_B3\\Java\\Game")
main_files = list((root / "core/src/main/java").rglob("*.java"))
all_files = main_files + list((root / "core/src/test/java").rglob("*.java")) + list((root / "lwjgl3/src/main/java").rglob("*.java"))
texts = [p.read_text(encoding="utf-8") for p in all_files]
for f in sorted(main_files):
    txt = f.read_text(encoding="utf-8")
    for m in re.finditer(r"public\s+(?:static\s+)?(?:final\s+)?[\w<>,\[\]\s.?]+\s+(\w+)\s*\(", txt):
        name = m.group(1)
        # skip constructors: method name equals class name
        class_m = re.search(r"\b(class|enum|interface)\s+(\w+)", txt)
        if class_m and name == class_m.group(2):
            continue
        count = sum(len(re.findall(r"\b" + re.escape(name) + r"\s*\(", t)) for t in texts)
        if count <= 1:
            print(f"POSSIBLY_UNUSED_METHOD {name} in {f}")
