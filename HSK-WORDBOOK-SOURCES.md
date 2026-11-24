# SHOHIN Chinese wordbook — sources and coverage

## Full HSK 3.0 vocabulary reference

File: `app/src/main/assets/hsk_vocabulary.tsv`

The app now includes all 11,000 numbered vocabulary entries from the HSK 3.0 syllabus transcription. Each row preserves:
- HSK band (1–6 or combined 7–9), assigned by the official syllabus sequence number;
- Chinese entry, including source disambiguation suffixes where present;
- tone-marked Pinyin;
- part of speech;
- original syllabus sequence number (1–11,000);
- original level label from the source transcription.

The sequence boundaries are 300, 500, 1,000, 2,000, 3,600, 5,400 and 11,000. The advanced band is one combined HSK 7–9 list.

Source transcription: https://github.com/chelsea6502/hsk-2025-data
Original syllabus source named by that project: ChineseTest / Center for Language Education and Cooperation, 2025 HSK examination syllabus: https://www.chinesetest.cn/syllabus

The imported file is a structured transcription of the syllabus vocabulary list, not an independently authored SHOHIN list. Source attribution is retained here. The source repository does not provide a clearly stated reuse license in its README; check the original syllabus and applicable dataset permissions before public commercial redistribution.

## Tajik/Russian meanings and examples

File: `app/src/main/assets/hsk_wordbook.tsv`

The starter pack contains 46 entries with:
- Chinese headword and tone-marked Pinyin;
- Tajik and Russian meanings;
- a Chinese example sentence;
- sentence Pinyin;
- Tajik and Russian sentence translations.

These 46 Tajik/Russian glosses and examples are editorial translations prepared for the app. They are not official HSK translations and have not yet been independently certified by native-speaker reviewers.

The app searches all 11,000 official vocabulary entries offline. Where one of the 46 starter entries matches, it also displays its Tajik/Russian meanings and example. Other entries currently show their official Chinese entry, Pinyin, level and part of speech; their Tajik/Russian meanings and examples still need to be added and reviewed.

## Accuracy rules

A Chinese character or word may have multiple readings and meanings. Pinyin and meaning must be attached to the word in context, not guessed from an isolated character. Preserve distinct source entries and readings; do not silently merge homographs.

## Related character datasets

- `hanzi_recognition.tsv`: 3,088 characters for recognition.
- `hanzi_writing.tsv`: 1,200 characters for writing.

These are separate from the 11,000-word vocabulary list.
