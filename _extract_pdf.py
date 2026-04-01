from pathlib import Path
import pypdf
pdf = Path(r"e:\\SDV_B3\\Java\\Game\\diapos.pdf")
out = Path(r"e:\\SDV_B3\\Java\\Game\\diapos_extracted.txt")
reader = pypdf.PdfReader(str(pdf))
text = "\n\n".join((p.extract_text() or "") for p in reader.pages)
out.write_text(text, encoding="utf-8")
print("OK", len(text), len(reader.pages))
