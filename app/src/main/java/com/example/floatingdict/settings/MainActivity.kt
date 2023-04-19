package com.example.floatingdict.settings

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.floatingdict.R
import com.example.floatingdict.data.db.DictDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainActivity : AppCompatActivity(R.layout.activity_main),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SettingsFragment>(R.id.fragment_container_view)
            }
        }
        title = "Settings"

        testDB()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Do nothing now because we don't have any sub-preferences.
        return true
    }

    private fun testDB() {
        lifecycleScope.launchWhenResumed {
            withContext(Dispatchers.IO) {
                val db = DictDatabase(context = getApplicationContext());
                val allWords: Cursor? = db.getWords()
                Timber.d("All words count: ${allWords?.count}")
            }
        }
    }
}
