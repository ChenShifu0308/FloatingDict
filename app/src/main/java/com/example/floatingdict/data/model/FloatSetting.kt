package com.example.floatingdict.data.model

data class FloatSetting(
    val draggable: Boolean,
    val darkMode: Boolean,
    val start: Int = 0,
    val end: Int = 0,
    val wordFontSize: String,
    val wordOrderRandom: Boolean,
    val lexiconSelect: String,
)
