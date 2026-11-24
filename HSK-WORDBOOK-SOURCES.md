# SHOHIN Chinese wordbook — sources and coverage

## Current offline pack

File: `app/src/main/assets/hsk_wordbook.tsv`

The first pack contains 46 beginner Chinese words. Each record contains:
- simplified Chinese headword;
- tone-marked Hanyu Pinyin;
- Tajik and Russian glosses;
- one short Chinese example sentence;
- Pinyin and Tajik/Russian translations of that sentence.

The Android dictionary searches all eight learner-facing fields. HSK character group screens show matching word examples when the character occurs in a word in this pack. Characters without a matching entry remain available for recognition and TextToSpeech practice; their dictionary meanings are not fabricated.

## Sources and editorial status

- HSK 3.0 word membership and level reference: the 2025 HSK syllabus and the public structured transcription at https://github.com/marpalo/hsk-vocabulary-dataset (CC BY 4.0 for its own dataset; CC BY-SA 4.0 applies to CC-CEDICT-derived definitions).
- Pinyin and lexical sense cross-check reference: CC-CEDICT, https://www.mdbg.net/chinese/dictionary?page=cc-cedict, CC BY-SA 4.0.
- The Tajik and Russian glosses and example sentences in this initial SHOHIN pack are editorial translations written for this app. They are not represented as official HSK translations or as independently certified native-speaker translations. They should receive native-speaker review before the pack is described as fully verified.

## Important distinction

A Chinese character may have multiple readings and meanings. The app therefore teaches it through real words and sentences. A single fixed translation attached to every isolated character would be misleading, especially for polyphonic characters.

## Next coverage targets

Expand the wordbook across all seven HSK 3.0 bands, preserve multiple readings as separate lexical records, add more example sentences, and keep source/license metadata with every imported dataset. The HSK recognition-character list (3,088 characters) and writing-character list (1,200 characters) remain separate official-syllabus datasets.
