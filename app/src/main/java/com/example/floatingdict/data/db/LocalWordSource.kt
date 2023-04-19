package com.example.floatingdict.data.db

import com.example.floatingdict.data.model.Word

interface LocalWordSource {
    fun getAllWords(): List<Word>
    fun getWordByBNCLevel(from: Int, to: Int): List<Word>
}
