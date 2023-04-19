package com.example.floatingdict.data.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper


class DictDatabase(context: Context) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    fun getWords(): Cursor {
        val db = readableDatabase
        val qb = SQLiteQueryBuilder()
        val sqlSelect = arrayOf(WORD_ID, WORD_CONTENT, PHONETIC_US, TRANSLATION, BNC_LEVEL)
        qb.tables = TABLE_NAME
        val c: Cursor = qb.query(
            db, sqlSelect, null, null,
            null, null, null
        )
        c.moveToFirst()
        return c
    }

    companion object {
        private const val DATABASE_NAME = "dict.sqlite"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "Common_Words"

        /*Column Keys*/
        private const val WORD_ID = "wordID"
        private const val WORD_CONTENT = "wordContent"
        private const val PHONETIC_EN = "phonetic_EN"
        private const val PHONETIC_US = "phonetic_US"
        private const val DEFINITION = "definition"
        private const val TRANSLATION = "translation"
        private const val WORD_TAGS = "wordTags"
        private const val WORD_EXCHANGES = "wordExchanges"
        private const val BNC_LEVEL = "bncLevel"
        private const val FRQ_LEVEL = "frqLevel"
        private const val COLLINS_LEVEL = "collinsLevel"
        private const val OXFORD_LEVEL = "oxfordLevel"
        private const val EXAMPLE_SENTENCES = "exampleSentences"
    }
}
