package com.example.floatingdict.data.model

data class FloatSetting(
    val draggable: Boolean,
    val darkMode: Boolean,
    val start: Int = 1,
    val end: Int = Int.MAX_VALUE,
)
