package com.example.floatingdict.settings

import android.content.Context
import android.content.SharedPreferences
import com.example.floatingdict.data.db.DictDatabase.Companion.MAX_WORD_LEVEL


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
        get() = mPrefs.getInt(KEY_WORD_INDEX_START, 1)
        set(index) {
            val editor = mPrefs.edit()
            editor.putInt(KEY_WORD_INDEX_START, index)
            editor.apply()
        }

    var wordIndexEnd: Int
        get() = mPrefs.getInt(KEY_WORD_INDEX_END, MAX_WORD_LEVEL)
        set(index) {
            val editor = mPrefs.edit()
            editor.putInt(KEY_WORD_INDEX_END, index)
            editor.apply()
        }

    companion object {
        const val PREFERENCES_FILE_NAME = "settings"

        private const val KEY_FLOAT_WINDOW_ENABLE = "float_window_enable"
        private const val KEY_FLOAT_WINDOW_DRAGGABLE = "float_window_draggable"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_WORD_INDEX_START = "word_index_start"
        private const val KEY_WORD_INDEX_END = "word_index_end"
    }
}
