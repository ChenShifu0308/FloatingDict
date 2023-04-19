package com.example.floatingdict.utils

import android.database.Cursor
import com.example.floatingdict.data.db.DictDatabase.Companion.BNC_LEVEL
import com.example.floatingdict.data.db.DictDatabase.Companion.COLLINS_LEVEL
import com.example.floatingdict.data.db.DictDatabase.Companion.DEFINITION
import com.example.floatingdict.data.db.DictDatabase.Companion.EXAMPLE_SENTENCES
import com.example.floatingdict.data.db.DictDatabase.Companion.FRQ_LEVEL
import com.example.floatingdict.data.db.DictDatabase.Companion.OXFORD_LEVEL
import com.example.floatingdict.data.db.DictDatabase.Companion.PHONETIC_EN
import com.example.floatingdict.data.db.DictDatabase.Companion.PHONETIC_US
import com.example.floatingdict.data.db.DictDatabase.Companion.TRANSLATION
import com.example.floatingdict.data.db.DictDatabase.Companion.WORD_CONTENT
import com.example.floatingdict.data.db.DictDatabase.Companion.WORD_EXCHANGES
import com.example.floatingdict.data.db.DictDatabase.Companion.WORD_ID
import com.example.floatingdict.data.db.DictDatabase.Companion.WORD_TAGS
import com.example.floatingdict.data.model.Word

internal fun Cursor.toSimpleWord(): Word.SimpleWord {
    val wordId = getColumnIndex(WORD_ID).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val wordContent = getColumnIndex(WORD_CONTENT).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val phoneticUS = getColumnIndex(PHONETIC_US).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val translation = getColumnIndex(TRANSLATION).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val bncLevel = getColumnIndex(BNC_LEVEL).takeIf { it > -1 }?.let { getInt(it) } ?: 0
    return Word.SimpleWord(wordId, wordContent, phoneticUS, translation, bncLevel)
}

internal fun Cursor.toFullWord(): Word.FullWord {
    val wordId = getColumnIndex(WORD_ID).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val wordContent = getColumnIndex(WORD_CONTENT).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val phoneticEN = getColumnIndex(PHONETIC_EN).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val phoneticUS = getColumnIndex(PHONETIC_US).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val definition = getColumnIndex(DEFINITION).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val translation = getColumnIndex(TRANSLATION).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val wordTags = getColumnIndex(WORD_TAGS).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val wordExchanges =
        getColumnIndex(WORD_EXCHANGES).takeIf { it > -1 }?.let { getString(it) } ?: ""
    val bncLevel = getColumnIndex(BNC_LEVEL).takeIf { it > -1 }?.let { getInt(it) } ?: 0
    val frqLevel = getColumnIndex(FRQ_LEVEL).takeIf { it > -1 }?.let { getInt(it) } ?: 0
    val collinsLevel = getColumnIndex(COLLINS_LEVEL).takeIf { it > -1 }?.let { getInt(it) } ?: 0
    val oxfordLevel = getColumnIndex(OXFORD_LEVEL).takeIf { it > -1 }?.let { getInt(it) } ?: 0
    val exampleSentences =
        getColumnIndex(EXAMPLE_SENTENCES).takeIf { it > -1 }?.let { getString(it) } ?: ""
    return Word.FullWord(
        wordId,
        wordContent,
        phoneticEN,
        phoneticUS,
        definition,
        translation,
        wordTags,
        wordExchanges,
        bncLevel,
        frqLevel,
        collinsLevel,
        oxfordLevel,
        exampleSentences
    )
}
