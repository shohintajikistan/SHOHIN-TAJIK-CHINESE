# SHOHIN Chinese wordbook — sources and coverage

## Full HSK 3.0 vocabulary reference

File: `app/src/main/assets/hsk_vocabulary.tsv`

The app includes all 11,000 numbered vocabulary entries from the selected HSK 3.0 2025 syllabus transcription. Each row preserves the HSK band, Chinese entry, tone-marked Pinyin, part of speech, syllabus sequence number and source level label.

The sequence boundaries are 300, 500, 1,000, 2,000, 3,600, 5,400 and 11,000. The advanced band is one combined HSK 7–9 list.

Sources:
- 2025 syllabus: ChineseTest / Center for Language Education and Cooperation — https://www.chinesetest.cn/syllabus
- Structured HSK 3.0 2025 vocabulary transcription: https://github.com/chelsea6502/hsk-2025-data
- An additional CC-CEDICT-derived English gloss dataset used by the wordbook generator: https://github.com/marpalo/hsk-vocabulary-dataset

The marpalo dataset transcription is CC BY 4.0. Its full English definitions derive from CC-CEDICT and are CC BY-SA 4.0. Keep the required attribution and ShareAlike notice when redistributing derived dictionary data. The HSK list remains the selected 2025 11,000-entry list; it is not replaced by the different 2026 HSK list.

## Tajik/Russian meanings and examples

File: `app/src/main/assets/hsk_wordbook.tsv`

The completed wordbook is designed to contain one row for each of the 11,000 HSK entries. The first eight columns are:
1. Chinese headword
2. Pinyin
3. Tajik meaning
4. Russian meaning
5. Chinese example sentence
6. Example sentence Pinyin
7. Tajik example translation
8. Russian example translation

Two provenance columns follow: `translation_status` and `example_source`. The app reads the first eight columns and continues to work with the added metadata.

The initial 46 SHOHIN starter entries are retained as editorial content. Remaining English glosses are sourced from CC-CEDICT-derived definitions and machine-translated into Tajik and Russian. Example sentences are taken where possible from the Zhongdex sentence corpus (https://github.com/saymei/zhongdex), which is based on a separate 2026 HSK corpus; these examples are matched to the selected 2025 headwords where possible. When no matching example is available, the generator creates a neutral example sentence and marks its source accordingly.

All automatic Tajik/Russian translations are marked `AUTO_TRANSLATED_UNREVIEWED`. They are not certified translations and require review by fluent speakers before being presented as fully verified educational material. The starter pack is marked separately and is also not independently certified.

The generation script is `scripts/fill_hsk_wordbook.py`; the manual GitHub Actions workflow is `.github/workflows/fill-wordbook.yml`. Translation calls are used only during dataset generation. The resulting TSV is bundled into the Android app, so dictionary lookup and examples work offline at runtime.

The Zhongdex data is licensed CC BY-SA 4.0; its sentence corpus and source attribution must be retained when redistributing derived examples. See https://github.com/saymei/zhongdex/blob/main/NOTICE and https://github.com/saymei/zhongdex/tree/main/data.

## Accuracy rules

A Chinese character or word may have multiple readings and meanings. Pinyin and meaning must be attached to the word in context, not guessed from an isolated character. Preserve distinct source entries and readings; do not silently merge homographs. Machine-generated translations and generated fallback examples must remain clearly identified as unreviewed.

## Related character datasets

- `hanzi_recognition.tsv`: 3,088 characters for recognition.
- `hanzi_writing.tsv`: 1,200 characters for writing.

These are separate from the 11,000-word vocabulary list.
