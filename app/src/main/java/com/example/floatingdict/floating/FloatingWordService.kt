package com.example.floatingdict.floating

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleService
import com.example.floatingdict.data.model.FloatSetting
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

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    fun setEnable(floatingEnable: Boolean) {

    }

    fun updateSettings(floatSetting: FloatSetting) {
        Timber.d("updateSettings: $floatSetting")

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
