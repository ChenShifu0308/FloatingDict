package com.example.floatingdict.data.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import androidx.annotation.WorkerThread
import com.example.floatingdict.data.model.Word
import com.example.floatingdict.utils.toSimpleWord
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import timber.log.Timber

class DictDatabase(context: Context) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION), LocalWordSource {

    private fun queryWords(
        projectionIn: Array<String>,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        groupBy: String? = null,
        having: String? = null,
        sortOrder: String? = null,
    ): List<Word> {
        val words: MutableList<Word.SimpleWord> = mutableListOf()

        val db = readableDatabase
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME
        val cursor: Cursor = qb.query(
            db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder
        )
        try {
            if (cursor.moveToFirst()) {
                do {
                    words.add(cursor.toSimpleWord())
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Timber.e("Error while trying to get words from database")
        } finally {
            if (!cursor.isClosed) {
                cursor.close()
            }
        }
        return words
    }

    @WorkerThread
    override fun getAllWords(): List<Word> {
        return queryWords(
            projectionIn = arrayOf(WORD_ID, WORD_CONTENT, PHONETIC_US, TRANSLATION, BNC_LEVEL),
            selection = "$BNC_LEVEL > ?",
            selectionArgs = arrayOf("0"),
            groupBy = null,
            having = null,
            sortOrder = null,
        )
    }

    override fun getWordByBNCLevel(from: Int, to: Int): List<Word> {
        if (from > to || from < 0) {
            return listOf()
        }
        val fromIndex = if (from == 0) 1 else from
        val toIndex = if (to > MAX_WORD_LEVEL) MAX_WORD_LEVEL else to

        return queryWords(
            projectionIn = arrayOf(WORD_ID, WORD_CONTENT, PHONETIC_US, TRANSLATION, BNC_LEVEL),
            selection = "$BNC_LEVEL BETWEEN ? AND ?",
            selectionArgs = arrayOf(fromIndex.toString(), toIndex.toString()),
            groupBy = null,
            having = null,
            sortOrder = "$BNC_LEVEL ASC",
        )
    }

    companion object {
        private const val DATABASE_NAME = "dict.sqlite"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "Common_Words"

        const val MAX_WORD_LEVEL = 20000


        /*Column Keys*/
        const val WORD_ID = "wordID"
        const val WORD_CONTENT = "wordContent"
        const val PHONETIC_EN = "phonetic_EN"
        const val PHONETIC_US = "phonetic_US"
        const val DEFINITION = "definition"
        const val TRANSLATION = "translation"
        const val WORD_TAGS = "wordTags"
        const val WORD_EXCHANGES = "wordExchanges"
        const val BNC_LEVEL = "bncLevel"
        const val FRQ_LEVEL = "frqLevel"
        const val COLLINS_LEVEL = "collinsLevel"
        const val OXFORD_LEVEL = "oxfordLevel"
        const val EXAMPLE_SENTENCES = "exampleSentences"

        private var instance: DictDatabase? = null
        public fun getInstance(context: Context): DictDatabase {
            if (instance == null) {
                instance = DictDatabase(context.applicationContext)
            }
            return instance!!
        }
    }
}
