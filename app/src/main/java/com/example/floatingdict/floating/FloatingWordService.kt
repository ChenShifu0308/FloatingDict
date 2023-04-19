package com.example.floatingdict.floating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.floatingdict.data.model.FloatSetting
import com.example.floatingdict.data.model.Word
import com.example.floatingdict.settings.AppSettings
import timber.log.Timber

/**
 * A foreground service that shows a floating word.
 */
class FloatingWordService : LifecycleService() {
    inner class LocalBinder : Binder() {
        fun getService(): FloatingWordService {
            return this@FloatingWordService
        }
    }

    private val binder = LocalBinder()
    private val appSettings: AppSettings by lazy { AppSettings(this) }

    override fun onCreate() {
        super.onCreate()
        FloatingManager.getInstance().init(this, appSettings)
        startNotification()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    fun setEnable(floatingEnable: Boolean) {
        FloatingManager.getInstance().setEnable(floatingEnable)
    }

    fun updateSettings(floatSetting: FloatSetting) {
        Timber.d("updateSettings: $floatSetting to the floating window.")
        FloatingManager.getInstance().updateSettings(floatSetting)
    }

    private fun updateWord(word: Word) {
        Timber.d("updateWord: $word to the floating window.")
        FloatingManager.getInstance().updateWord(word)
    }

    /**
     * Used for creating and starting notification
     * whenever we start our Bound service
     */
    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle("A service is running in the background")
                .setContentText("Generating random number").build()
            startForeground(1, notification)
        } else {
            Timber.w("VERSION.SDK_INT < O")
        }
    }
}

private class FloatingOnTouchListener : View.OnTouchListener {
    private var x = 0
    private var y = 0
    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x = event.rawX.toInt()
                y = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val nowX = event.rawX.toInt()
                val nowY = event.rawY.toInt()
                val movedX = nowX - x
                val movedY = nowY - y
                x = nowX
                y = nowY
//                layoutParams.x = layoutParams.x + movedX
//                layoutParams.y = layoutParams.y + movedY
//                windowManager.updateViewLayout(view, layoutParams)
            }
            else -> {}
        }
        return false
    }
}
