package com.example.floatingdict.settings

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.os.HandlerCompat.postDelayed
import androidx.preference.*
import com.example.floatingdict.R
import com.example.floatingdict.floating.FloatingWordService
import com.example.floatingdict.floating.permission.OnPermissionResult
import com.example.floatingdict.floating.permission.PermissionUtils
import com.example.floatingdict.settings.AppSettings.Companion.PREFERENCES_FILE_NAME
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {
    /*TODO: use ViewModel to make them responsible*/
    var permissionButton: Preference? = null
    var enableSettings: SwitchPreferenceCompat? = null
    var startIndexPreference: EditTextPreference? = null
    var endIndexPreference: EditTextPreference? = null

    private var floatingWordService: FloatingWordService? = null

    private val appSettings: AppSettings by lazy {
        AppSettings(requireContext())
    }
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName, service: IBinder
        ) {
            floatingWordService = (service as FloatingWordService.LocalBinder).getService()
            if (appSettings.isFloatWindowEnabled) {
                floatingWordService?.setEnable(true)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            floatingWordService = null
        }
    }

    override fun onResume() {
        super.onResume()
        appSettings.isFloatWindowDraggable = true
        floatingWordService?.updateSettings(appSettings.getFloatSetting())
    }

    override fun onStop() {
        super.onStop()
        appSettings.isFloatWindowDraggable = false
        floatingWordService?.updateSettings(appSettings.getFloatSetting())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val manager: PreferenceManager = preferenceManager
        manager.sharedPreferencesName = PREFERENCES_FILE_NAME
        setPreferencesFromResource(R.xml.settings, rootKey)
        setupPreferences()
        checkPermission(requireActivity())
        bindBleService()
    }

    private fun setupPreferences() {
        /* Permission button*/
        permissionButton = findPreference(AppSettings.KEY_PERMISSION_BUTTON)
        permissionButton?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            Timber.d("Permission button clicked")
            PermissionUtils.requestPermission(
                requireActivity(),
                object : OnPermissionResult {
                    override fun permissionResult(isOpen: Boolean) {
                        Timber.d("permissionResult: $isOpen")
                        enableSettings?.isEnabled = isOpen
                        permissionButton?.isEnabled = !isOpen
                        permissionButton?.summary = getString(R.string.permission_granted)
                    }
                })
            true
        }

        /* Enable floating window switch*/
        enableSettings = findPreference(AppSettings.KEY_FLOAT_WINDOW_ENABLE)
        enableSettings?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                Timber.d("enableSettings newValue: $newValue")
                floatingWordService?.setEnable(newValue == true)
                true
            }

        findPreference<SwitchPreferenceCompat>(AppSettings.KEY_DARK_MODE)?.onPreferenceChangeListener =
            updateListeners
        startIndexPreference = findPreference<EditTextPreference>(AppSettings.KEY_WORD_INDEX_START)
        startIndexPreference?.onPreferenceChangeListener =
            updateListeners
        endIndexPreference = findPreference<EditTextPreference>(AppSettings.KEY_WORD_INDEX_END)
        endIndexPreference?.onPreferenceChangeListener =
            updateListeners
        if(appSettings.lexiconSelect != "all") {
            startIndexPreference?.isEnabled = false
            endIndexPreference?.isEnabled = false
        }

        findPreference<ListPreference>(AppSettings.KEY_WORD_FONT_SIZE)?.onPreferenceChangeListener =
            updateListeners
        findPreference<SwitchPreferenceCompat>(AppSettings.KEY_WORD_ORDER_RANDOM)?.onPreferenceChangeListener =
            updateListeners
        findPreference<ListPreference>(AppSettings.KEY_WORD_LEXICON_SELECT)?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                if (newValue != "all") {
                    startIndexPreference?.isEnabled = false
                    endIndexPreference?.isEnabled = false
                } else {
                    startIndexPreference?.isEnabled = true
                    endIndexPreference?.isEnabled = true
                }
                updateListeners.onPreferenceChange(preference, newValue)
                true
            }

    }

    private val updateListeners: Preference.OnPreferenceChangeListener =
        Preference.OnPreferenceChangeListener { preference, newValue ->
            Timber.d("Settings newValue: $newValue")
            updateSettings()
            true
        }

    private fun updateSettings() {
        Handler().post() {
            floatingWordService?.updateSettings(appSettings.getFloatSetting())
        }
    }

    private fun checkPermission(activity: Activity) {
        if (PermissionUtils.checkPermission(activity)) {
            enableSettings?.isEnabled = true
            permissionButton?.isEnabled = false
            permissionButton?.summary = getString(R.string.permission_granted)
        } else {
            enableSettings?.isChecked = false
            enableSettings?.isEnabled = false
            permissionButton?.isEnabled = true
            permissionButton?.summary = getString(R.string.open_permission)
            Timber.d("init: no permission")
        }
    }

    private fun bindBleService() {
        val gattServiceIntent = Intent(requireContext(), FloatingWordService::class.java)
        requireContext().bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}
