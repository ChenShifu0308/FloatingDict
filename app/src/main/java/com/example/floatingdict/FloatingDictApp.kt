package com.example.floatingdict

import android.app.Application
import com.example.floatingdict.BuildConfig
import timber.log.Timber

class FloatingDictApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
