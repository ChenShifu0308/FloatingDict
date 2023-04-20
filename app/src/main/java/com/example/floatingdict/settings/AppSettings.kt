package com.example.floatingdict.settings

import android.content.Context
import android.content.SharedPreferences
import com.example.floatingdict.data.db.DictDatabase.Companion.MAX_WORD_LEVEL
import com.example.floatingdict.data.model.FloatSetting

/**
 * The app settings wrapping a [SharedPreferences] instance.
 */
class AppSettings(context: Context) {
    private val mPrefs: SharedPreferences

    init {
        mPrefs = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    var isFloatWindowEnabled: Boolean
        get() = mPrefs.getBoolean(KEY_FLOAT_WINDOW_ENABLE, true)
        set(enabled) {
            val editor = mPrefs.edit()
            editor.putBoolean(KEY_FLOAT_WINDOW_ENABLE, enabled)
            editor.apply()
        }
    var isFloatWindowDraggable: Boolean
        get() = mPrefs.getBoolean(KEY_FLOAT_WINDOW_DRAGGABLE, true)
        set(draggable) {
            val editor = mPrefs.edit()
            editor.putBoolean(KEY_FLOAT_WINDOW_DRAGGABLE, draggable)
            editor.apply()
        }

    var isDarkMode: Boolean
        get() = mPrefs.getBoolean(KEY_DARK_MODE, false)
        set(darkMode) {
            val editor = mPrefs.edit()
            editor.putBoolean(KEY_DARK_MODE, darkMode)
            editor.apply()
        }

    var wordIndexStart: Int
        get() = parseIntValue(mPrefs.getString(KEY_WORD_INDEX_START, "0")!!)
        set(index) {
            val editor = mPrefs.edit()
            editor.putString(KEY_WORD_INDEX_START, index.toString())
            editor.apply()
        }


    var wordIndexEnd: Int
        get() = parseIntValue(mPrefs.getString(KEY_WORD_INDEX_END, MAX_WORD_LEVEL.toString())!!)
        set(index) {
            val editor = mPrefs.edit()
            editor.putInt(KEY_WORD_INDEX_END, index)
            editor.apply()
        }

    var wordFontSize: String
        get() = mPrefs.getString(KEY_WORD_FONT_SIZE, "normal")!!
        set(value) {
            val editor = mPrefs.edit()
            editor.putString(KEY_WORD_FONT_SIZE, value)
            editor.apply()
        }

    var wordOrderRandom: Boolean
        get() = mPrefs.getBoolean(KEY_WORD_ORDER_RANDOM, false)
        set(random) {
            val editor = mPrefs.edit()
            editor.putBoolean(KEY_WORD_ORDER_RANDOM, random)
            editor.apply()
        }

    var lexiconSelect: String
        get() = mPrefs.getString(KEY_WORD_LEXICON_SELECT, "all")!!
        set(value) {
            val editor = mPrefs.edit()
            editor.putString(KEY_WORD_LEXICON_SELECT, value)
            editor.apply()
        }


    fun getFloatSetting(): FloatSetting = FloatSetting(
        isFloatWindowDraggable,
        isDarkMode,
        wordIndexStart,
        wordIndexEnd,
        wordFontSize,
        wordOrderRandom,
        lexiconSelect
    )

    private fun parseIntValue(intString: String): Int =
        try {
            Integer.parseInt(intString)
        } catch (e: NumberFormatException) {
            0
        }

    companion object {
        const val PREFERENCES_FILE_NAME = "settings"

        const val KEY_PERMISSION_BUTTON = "permission_button"
        const val KEY_FLOAT_WINDOW_ENABLE = "float_window_enable"
        const val KEY_FLOAT_WINDOW_DRAGGABLE = "float_window_draggable"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_WORD_INDEX_START = "word_index_start"
        const val KEY_WORD_INDEX_END = "word_index_end"

        const val KEY_WORD_FONT_SIZE = "font_size"
        const val KEY_WORD_ORDER_RANDOM= "order_random"
        const val KEY_WORD_LEXICON_SELECT = "lexicon_select"
    }
}
