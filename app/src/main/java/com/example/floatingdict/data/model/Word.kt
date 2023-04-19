package com.example.floatingdict.data.model

sealed class Word(
    open val wordID: String,
    open val wordContent: String,
    open val phonetic_US: String,
    open val translation: String,
    open val bncLevel: Int,
) {
    fun toFloatingString(): String {
        return " $phonetic_US  ${translation.replace("n", "")}"
    }

    data class SimpleWord(
        override val wordID: String,
        override val wordContent: String,
        override val phonetic_US: String,
        override val translation: String,
        override val bncLevel: Int,
    ) : Word(wordID, wordContent, phonetic_US, translation, bncLevel)

    data class FullWord(
        override val wordID: String,
        override val wordContent: String,
        val phonetic_EN: String,
        override val phonetic_US: String,
        val definition: String,
        override val translation: String,
        val wordTags: String,
        val wordExchanges: String,
        override val bncLevel: Int,
        val frqLevel: Int,
        val collinsLevel: Int,
        val oxfordLevel: Int,
        val exampleSentences: String,
    ) : Word(wordID, wordContent, phonetic_US, translation, bncLevel)
}
