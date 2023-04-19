package com.example.floatingdict.settings

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
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

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName, service: IBinder
        ) {
            floatingWordService = (service as FloatingWordService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            floatingWordService = null
        }
    }

    private val appSettings: AppSettings by lazy {
        AppSettings(requireContext())
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

        /*TODO: we can set it to be draggable only when this fragment is on the top*/
        val draggableSettings: SwitchPreferenceCompat? =
            findPreference(AppSettings.KEY_FLOAT_WINDOW_DRAGGABLE)
        draggableSettings?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                Timber.d("draggableSettings newValue: $newValue")
                floatingWordService?.updateSettings(appSettings.getFloatSetting())
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
