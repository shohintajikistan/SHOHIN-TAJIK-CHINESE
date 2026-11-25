#!/usr/bin/env python3
"""Build independently downloadable SHOHIN HSK level packages for GitHub Pages."""
import csv
import json
import math
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app" / "src" / "main" / "assets"
OUT = ROOT / "data"
OUT.mkdir(exist_ok=True)

def read_tsv(path):
    with path.open("r", encoding="utf-8-sig", newline="") as handle:
        return list(csv.reader(handle, delimiter="\t"))

vocab_rows = read_tsv(ASSETS / "hsk_vocabulary.tsv")
book_rows = read_tsv(ASSETS / "hsk_wordbook.tsv")
if len(vocab_rows) < 2:
    raise SystemExit("Vocabulary TSV is empty or has no data rows.")

translations = {}
for row in book_rows[1:]:
    if len(row) >= 2:
        key = row[0] + "|" + row[1].lower()
        translations[key] = {
            "tg": row[2] if len(row) > 2 and row[2] else "Тарҷума ҳоло дастрас нест",
            "ru": row[3] if len(row) > 3 and row[3] else "Перевод пока отсутствует",
            "example": row[4] if len(row) > 4 else "",
            "examplePin": row[5] if len(row) > 5 else "",
            "exampleTg": row[6] if len(row) > 6 else "",
            "exampleRu": row[7] if len(row) > 7 else "",
            "translationStatus": row[8] if len(row) > 8 else "",
        }

all_words = []
for index, row in enumerate(vocab_rows[1:]):
    if len(row) < 3:
        continue
    source_level = int(row[0])
    han = row[1]
    pin = row[2]
    extra = translations.get(han + "|" + pin.lower(), {})
    all_words.append({
        "id": str(source_level) + ":" + (row[4] if len(row) > 4 and row[4] else str(index)),
        "sourceLevel": source_level,
        "han": han,
        "pin": pin,
        "pos": row[3] if len(row) > 3 else "",
        "tg": extra.get("tg", "Тарҷума ҳоло дастрас нест"),
        "ru": extra.get("ru", "Перевод пока отсутствует"),
        "example": extra.get("example", ""),
        "examplePin": extra.get("examplePin", ""),
        "exampleTg": extra.get("exampleTg", ""),
        "exampleRu": extra.get("exampleRu", ""),
        "translationStatus": extra.get("translationStatus", ""),
    })

groups = [[w for w in all_words if w["sourceLevel"] == level] for level in range(1, 7)]
advanced = [w for w in all_words if w["sourceLevel"] >= 7]
cut1 = math.ceil(len(advanced) / 3)
cut2 = math.ceil(len(advanced) * 2 / 3)
groups.extend([advanced[:cut1], advanced[cut1:cut2], advanced[cut2:]])

manifest = {"totalWords": len(all_words), "levels": []}
for index, group in enumerate(groups):
    filename = f"hsk-level-{index + 1}.json"
    payload = {"level": index + 1, "words": group}
    (OUT / filename).write_text(
        json.dumps(payload, ensure_ascii=False, separators=(",", ":")),
        encoding="utf-8"
    )
    manifest["levels"].append({
        "index": index,
        "name": f"HSK {index + 1}",
        "wordCount": len(group),
        "lessonCount": math.ceil(len(group) / 15),
        "file": filename,
    })

(OUT / "levels.json").write_text(
    json.dumps(manifest, ensure_ascii=False, separators=(",", ":")),
    encoding="utf-8"
)
print(f"Built {len(groups)} level packages containing {len(all_words)} words.")
