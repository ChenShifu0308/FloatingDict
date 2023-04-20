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
import java.util.*

/**
 * A foreground service that shows a floating word.
 */
class FloatingWordService : LifecycleService() {
    inner class LocalBinder : Binder() {
        fun getService(): FloatingWordService {
            return this@FloatingWordService
        }
    }

    private var currentLexicon: Lexicon? = null
    private var currentIndex: Int = 0
    private val random = Random()
    private val updateWordRunnable: Runnable = object : Runnable {
        override fun run() {
            /*Select one word which is not shown before*/
            val words: List<Word> = allWords ?: return
            val length = words.size
            val selectedIndex = if (appSettings.wordOrderRandom) {
                random.nextInt(length)
            } else {
                currentIndex++ % length
            }
            Timber.d("selected index is $selectedIndex")
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
        initWordList(Lexicon.fromName(appSettings.lexiconSelect))
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
        val newLexicon = Lexicon.fromName(floatSetting.lexiconSelect)
        if (currentLexicon != newLexicon) {
            initWordList(newLexicon)
        } else if (newLexicon == Lexicon.All) {
            // Update the word list if the word index range is changed.
            if (startIndex == floatSetting.start && endIndex == floatSetting.end) {
                Timber.d("No need to update word list.")
            } else {
                currentIndex = 0 // Reset the index.
                initWordList(Lexicon.fromName(appSettings.lexiconSelect))
            }
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

    private fun initWordList(lexicon: Lexicon) {
        currentLexicon = lexicon
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                val db = DictDatabase.getInstance(applicationContext)
                startIndex = appSettings.wordIndexStart
                endIndex = appSettings.wordIndexEnd
                allWords = getWordListFromDb(db, lexicon, startIndex, endIndex)
                Timber.d("All words count: ${allWords?.size}")
            }
        }
    }

    private fun getWordListFromDb(
        db: DictDatabase,
        lexicon: Lexicon,
        startIndex: Int,
        endIndex: Int
    ): List<Word>? {
        return when (lexicon) {
            Lexicon.All -> {
                if (startIndex <= 0 || endIndex <= 0 || startIndex >= endIndex || startIndex >= MAX_WORD_LEVEL) {
                    Timber.e("Invalid word index range: $startIndex - $endIndex")
                    db.getAllWords()
                } else {
                    db.getWordByBNCLevel(from = startIndex, to = endIndex)
                }
            }
            Lexicon.CET,
            Lexicon.IELTS,
            Lexicon.TOEFL,
            Lexicon.GRE,
            Lexicon.CET4,
            Lexicon.CET6 -> {
                db.getAllWords(lexicon.name)
            }
        }
    }

    companion object {
        private const val UPDATE_DURATION = 5000L
    }
}

enum class Lexicon(name: String) {
    All("all"),
    CET("cet"),
    IELTS("ielts"),
    TOEFL("toefl"),
    GRE("gre"),
    CET4("cet4"),
    CET6("cet6");

    companion object {
        fun fromName(name: String): Lexicon {
            return when (name) {
                "all" -> All
                "cet" -> CET
                "ielts" -> IELTS
                "toefl" -> TOEFL
                "gre" -> GRE
                "cet4" -> CET4
                "cet6" -> CET6
                else -> All
            }
        }
    }

}
