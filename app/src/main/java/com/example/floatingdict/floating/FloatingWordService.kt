package com.example.floatingdict.floating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.floatingdict.data.db.DictDatabase
import com.example.floatingdict.data.db.DictDatabase.Companion.MAX_WORD_LEVEL
import com.example.floatingdict.data.model.FloatSetting
import com.example.floatingdict.data.model.Word
import com.example.floatingdict.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    private var currentIndex: Int = 0
    private val updateWordRunnable: Runnable = object : Runnable {
        override fun run() {
            /*Select one word which is not shown before*/
            val words: List<Word> = allWords ?: return
            val length = words.size
            val selectedIndex = currentIndex++ % length
            updateWord(words[selectedIndex])
            handler.postDelayed(this, UPDATE_DURATION)
        }
    }

    private var allWords: List<Word>? = null
        set(value) {
            field = value
            if (appSettings.isFloatWindowEnabled) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(updateWordRunnable, UPDATE_DURATION)
            }
        }
    private var startIndex: Int = 0
    private var endIndex: Int = 0
    private val binder = LocalBinder()
    private val appSettings: AppSettings by lazy { AppSettings(this) }
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

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
        initWordList()
        FloatingManager.getInstance().setEnable(floatingEnable)

        if (floatingEnable) {
            if (allWords != null) {
                handler.postDelayed(updateWordRunnable, UPDATE_DURATION)
            } else {
                handler.removeCallbacksAndMessages(null)
            }
        }
    }

    fun updateSettings(floatSetting: FloatSetting) {
        Timber.d("updateSettings: $floatSetting to the floating window.")
        FloatingManager.getInstance().updateSettings(floatSetting)
        if (startIndex == floatSetting.start && endIndex == floatSetting.end) {
            Timber.d("No need to update word list.")
        } else {
            // Refresh the word list.
            currentIndex = 0 // Reset the index.
            initWordList()
        }
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
                "CHANNEL_ID", "My Channel", NotificationManager.IMPORTANCE_DEFAULT
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

    private fun initWordList() {
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                val db = DictDatabase.getInstance(applicationContext)
                startIndex = appSettings.wordIndexStart
                endIndex = appSettings.wordIndexEnd
                allWords =
                    if (startIndex <= 0 || endIndex <= 0 || startIndex >= endIndex || startIndex >= MAX_WORD_LEVEL) {
                        Timber.e("Invalid word index range: $startIndex - $endIndex")
                        db.getAllWords()
                    } else {
                        db.getWordByBNCLevel(from = startIndex, to = endIndex)
                    }
                Timber.d("All words count: ${allWords?.size}")
            }
        }
    }

    companion object {
        private const val UPDATE_DURATION = 5000L
    }
}
