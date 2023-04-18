# Floating Dictionary

I always want to have a dictionary to have the feature of reciting words with a floating window,
which can be always on top of other windows.
Then I can recite words while I am doing other things.
So I made this one.

# MVP (Minimum Viable Product)

The first edition of this dictionary is a MVP (Minimum Viable Product).I want to make it very simple
but can prefectly meet my needs.
It has the basic features below:

- A floating window to show the meaning of a word. the floating window can be always on top of other
  windows. and the words would stay for a while before change to the next word.
- A word list to recite. I choose to use the frequency order to show the words. while I can
  configure the index of start and end.
- The words shown in the floating window can be recited in random order or in order. but it should
  record the shown words and not show them again until all the words in the list have been shown.
  the records would be reset after the word list configuration is changed.

#### Prototype

[![Prototype](/doc/prototype.jpg)]

#### Run

- unzip the dict.sqlite.zip file in the assets folder.(The original file is too large to upload to
  github, and I do not want to up git lfs.)
-

# References

#### Dictionary sqlite database

- The dictionary sqlite database is from https://github.com/dyeeee/English-Chinese-Dictionary.

|  字段名  | 字段说明  |
|  ----  | ----  |
| wordID  | 单词ID，主键 |
| wordContent  | 单词 |
| phonetic_EN  | 英式英标 |
| phonetic_US  | 美式英标 |
| definition  | 英英释义 |
| translation  | 英含释义 |
| wordTags  | 单词标记（四六级/雅思等） |
| wordExchanges  | 时态复数等词形变换 |
| bncLevel  | 英国国家语料库词频顺序（1为频率最高） |
| frqLevel  | 当代语料库词频顺序（1为频率最高） |
| collinsLevel  | 柯林斯星级（1-5星，5星为频率最高） |
| oxfordLevel  | 是否牛津三千核心词汇 |
| exampleSentences  | 例句 |

#### Read sqlite file in assets folder

- https://github.com/jgilfelt/android-sqlite-asset-helper

#### Floating

- https://github.com/princekin-f/EasyFloat
