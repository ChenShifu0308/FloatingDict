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
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
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

        val darkModeSwitch: SwitchPreferenceCompat? =
            findPreference(AppSettings.KEY_DARK_MODE)
        darkModeSwitch?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                Timber.d("Dark mode settings newValue: $newValue")
                Handler().post() {
                    floatingWordService?.updateSettings(appSettings.getFloatSetting())
                }
                true
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
