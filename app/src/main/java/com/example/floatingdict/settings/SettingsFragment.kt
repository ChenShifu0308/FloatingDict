package com.example.floatingdict.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.floatingdict.R

class SettingsFragment : PreferenceFragmentCompat() {
    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val manager: PreferenceManager = preferenceManager
        manager.sharedPreferencesName = PREFERENCES_FILE_NAME
        setPreferencesFromResource(R.xml.settings, rootKey)
        setupPreferences()
    }

    private fun setupPreferences() {
        val enableSettings: SwitchPreferenceCompat? = findPreference("float_window_enable")
        enableSettings?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                Log.d("SettingsFragment", "enableSettings newValue: $newValue")
                if (newValue == true) {

                } else {
                }
                true
            }

        val draggableSettings: SwitchPreferenceCompat? = findPreference("float_window_draggable")
        draggableSettings?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                Log.d("SettingsFragment", "draggableSettings newValue: $newValue")
                if (newValue == true) {

                } else {
                }
                true
            }
    }

    companion object {
        const val PREFERENCES_FILE_NAME = "settings"
    }
}
