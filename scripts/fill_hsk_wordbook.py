#!/usr/bin/env python3
"""Build the complete HSK 3.0 2025 Tajik/Russian wordbook.

Translations are machine-generated and explicitly marked unreviewed.
"""
import csv
import io
import json
import re
import sys
import time
import urllib.parse
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app" / "src" / "main" / "assets"
VOCAB = ASSETS / "hsk_vocabulary.tsv"
OUTPUT = ASSETS / "hsk_wordbook.tsv"
SEP = "ZXQSHOHINSEPZXQ"
UA = "SHOHIN-TAJIK-CHINESE-wordbook-builder/1.0"

def get_bytes(url):
    req = urllib.request.Request(url, headers={"User-Agent": UA})
    with urllib.request.urlopen(req, timeout=90) as response:
        return response.read()

def clean(value):
    return re.sub(r"[\t\r\n]+", " ", str(value or "")).strip()

def key_word(value):
    return re.sub(r"\d+$", "", value).strip()

def load_vocab():
    rows = []
    with VOCAB.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f, delimiter="\t"):
            rows.append(row)
    if len(rows) != 11000:
        raise RuntimeError(f"Expected 11000 HSK rows, found {len(rows)}")
    return rows

def load_cedict_glosses():
    glosses = {}
    base = "https://raw.githubusercontent.com/marpalo/hsk-vocabulary-dataset/main/data/"
    files = [f"hsk30-words-{i}.json" for i in range(1, 7)] + ["hsk30-words-7-9.json"]
    for filename in files:
        print("Downloading dictionary meanings:", filename, flush=True)
        payload = json.loads(get_bytes(base + filename).decode("utf-8"))
        for item in payload.get("items", []):
            word = key_word(item.get("word", ""))
            meanings = []
            for definition in item.get("full_definitions", []):
                for meaning in definition.get("meanings", []):
                    meaning = clean(meaning)
                    if meaning and meaning not in meanings and not meaning.startswith("CL:"):
                        meanings.append(meaning)
            if word and meanings:
                glosses.setdefault(word, "; ".join(meanings[:8]))
    return glosses

def load_examples():
    url = "https://raw.githubusercontent.com/saymei/zhongdex/main/data/sentences.jsonl"
    print("Downloading example sentence corpus (2026 HSK source)...", flush=True)
    examples = {}
    request = urllib.request.Request(url, headers={"User-Agent": UA})
    with urllib.request.urlopen(request, timeout=180) as response:
        for raw in response:
            try:
                record = json.loads(raw.decode("utf-8"))
            except Exception:
                continue
            hanzi = clean(record.get("hanzi"))
            pinyin = clean(record.get("pinyin"))
            english = clean(record.get("english"))
            if not hanzi or not pinyin or not english:
                continue
            for link in record.get("headwords", []):
                word = key_word(link.get("simplified", ""))
                if not word:
                    continue
                old = examples.get(word)
                slot = link.get("slot", "")
                rank = 0 if slot == "atLevel" else 1 if slot == "easy" else 2
                if old is None or rank < old[0]:
                    examples[word] = (rank, hanzi, pinyin, english)
    print("Matched example sentences:", len(examples), flush=True)
    return examples

class BrowserTranslator:
    def __enter__(self):
        from playwright.sync_api import sync_playwright
        self.playwright = sync_playwright().start()
        self.browser = self.playwright.chromium.launch(
            headless=True, args=["--no-sandbox", "--disable-dev-shm-usage"]
        )
        self.page = self.browser.new_page()
        self.page.goto("https://translate.google.com", wait_until="domcontentloaded", timeout=60000)
        return self

    def call(self, text, target):
        return self.page.evaluate("""async ({text, target}) => {
            const params = new URLSearchParams({
                client: "gtx", sl: "auto", tl: target, dt: "t", q: text
            });
            const response = await fetch(
                "https://translate.googleapis.com/translate_a/single?" + params.toString()
            );
            if (!response.ok) throw new Error("HTTP " + response.status);
            const data = await response.json();
            return (data[0] || []).map(part => part && part[0] ? part[0] : "").join("");
        }""", {"text": text, "target": target})

    def __exit__(self, exc_type, exc, tb):
        self.browser.close()
        self.playwright.stop()

def translate_batch(texts, target, translator):
    results = []
    batch_size = 18
    for start in range(0, len(texts), batch_size):
        batch = texts[start:start + batch_size]
        packed = ("\n" + SEP + "\n").join(batch)
        translated = None
        for attempt in range(5):
            try:
                candidate = translator.call(packed, target)
                pieces = candidate.split(SEP)
                if len(pieces) == len(batch):
                    translated = [clean(x) for x in pieces]
                    break
            except Exception as error:
                print(f"Batch retry {attempt + 1}: {error}", flush=True)
            time.sleep(2 + attempt * 2)
        if translated is None:
            translated = []
            for item in batch:
                last_error = None
                for attempt in range(5):
                    try:
                        translated.append(clean(translator.call(item, target)))
                        last_error = None
                        break
                    except Exception as error:
                        last_error = error
                        time.sleep(2 + attempt * 2)
                if last_error is not None:
                    raise RuntimeError(f"Translation failed for {item!r}: {last_error}")
        results.extend(translated)
        if start % 180 == 0:
            print(f"{target}: translated {min(start + len(batch), len(texts))}/{len(texts)}", flush=True)
        time.sleep(0.15)
    return results

def load_curated():
    curated = {}
    if not OUTPUT.exists():
        return curated
    with OUTPUT.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f, delimiter="\t"):
            if row.get("word"):
                curated[row["word"]] = row
    return curated

def main():
    rows = load_vocab()
    glosses = load_cedict_glosses()
    examples = load_examples()
    curated = load_curated()

    records = []
    translation_inputs = []
    translation_targets = []
    for row in rows:
        word = clean(row["word"])
        plain = key_word(word)
        pinyin = clean(row["pinyin"])
        old = curated.get(word)
        if old and all(old.get(k) for k in ("tajik", "russian", "example_zh", "example_pinyin", "example_tajik", "example_russian")):
            records.append({
                "word": word, "pinyin": pinyin,
                "tajik": old["tajik"], "russian": old["russian"],
                "example_zh": old["example_zh"], "example_pinyin": old["example_pinyin"],
                "example_tajik": old["example_tajik"], "example_russian": old["example_russian"],
                "status": "EDITORIAL_STARTER_UNREVIEWED", "source": "SHOHIN starter pack"
            })
            continue

        meaning_en = glosses.get(plain, plain)
        example = examples.get(plain)
        if example:
            _, example_zh, example_pinyin, example_en = example
            source = "Zhongdex 2026 sentence corpus; mapped by headword"
        else:
            example_zh = f"我今天学习“{plain}”这个词。"
            example_pinyin = f"Wǒ jīntiān xuéxí {pinyin} zhège cí."
            example_en = f"Today I am learning the Chinese word {plain}."
            source = "Generated neutral example template"
        records.append({
            "word": word, "pinyin": pinyin, "meaning_en": meaning_en,
            "example_zh": example_zh, "example_pinyin": example_pinyin,
            "example_en": example_en, "status": "AUTO_TRANSLATED_UNREVIEWED",
            "source": source
        })
        translation_inputs.extend([meaning_en, example_en])
        translation_targets.extend([len(records) - 1, len(records) - 1])

    missing = [r for r in records if "meaning_en" in r]
    if missing:
        texts = []
        for r in missing:
            texts.extend([r["meaning_en"], r["example_en"]])
        print("Machine translating", len(texts), "text fields into Tajik and Russian", flush=True)
        with BrowserTranslator() as translator:
            tg = translate_batch(texts, "tg", translator)
            ru = translate_batch(texts, "ru", translator)
        for i, record in enumerate(missing):
            record["tajik"] = tg[i * 2] or record["meaning_en"]
            record["example_tajik"] = tg[i * 2 + 1] or record["example_en"]
            record["russian"] = ru[i * 2] or record["meaning_en"]
            record["example_russian"] = ru[i * 2 + 1] or record["example_en"]

    fields = ["word", "pinyin", "tajik", "russian", "example_zh",
              "example_pinyin", "example_tajik", "example_russian",
              "translation_status", "example_source"]
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    with OUTPUT.open("w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f, delimiter="\t", lineterminator="\n")
        writer.writerow(fields)
        for record in records:
            writer.writerow([
                clean(record["word"]), clean(record["pinyin"]),
                clean(record.get("tajik")), clean(record.get("russian")),
                clean(record["example_zh"]), clean(record["example_pinyin"]),
                clean(record.get("example_tajik")), clean(record.get("example_russian")),
                clean(record["status"]), clean(record["source"])
            ])

    if len(records) != 11000:
        raise RuntimeError(f"Output has {len(records)} entries, expected 11000")
    print(f"SUCCESS: wrote {len(records)} complete entries to {OUTPUT}", flush=True)

if __name__ == "__main__":
    main()
