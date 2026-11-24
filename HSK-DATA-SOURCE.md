# HSK 3.0 character data

This app includes the HSK 3.0 character recognition and writing lists sourced from `chelsea6502/hsk-2025-data`, which identifies `chinesetest.cn` as the syllabus source.

- `app/src/main/assets/hanzi_recognition.tsv`: 3,088 cumulative recognition characters across HSK 1–6 and HSK 7–9.
- `app/src/main/assets/hanzi_writing.tsv`: 1,200 cumulative writing characters across the same bands.

The recognition list is grouped as HSK1, HSK2, HSK3, HSK4, HSK5, HSK6 and HSK7-9. HSK 7–9 is one combined band in the 2025 syllabus. Character-only source data does not contain Pinyin, Tajik definitions, example words or audio files; these must be added from verified lexical sources rather than guessed. Mandarin audio can be provided by Android TextToSpeech when a Chinese voice is installed.

Source repository: https://github.com/chelsea6502/hsk-2025-data
