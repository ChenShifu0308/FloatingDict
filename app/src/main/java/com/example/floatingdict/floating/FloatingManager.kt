package com.example.floatingdict.floating

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.floatingdict.R
import com.example.floatingdict.data.model.FloatSetting
import com.example.floatingdict.data.model.Word
import com.example.floatingdict.floating.view.FloatingView
import com.example.floatingdict.settings.AppSettings

/**
 * A manager that manages the floating view.
 */
class FloatingManager {
    private var isFloatingViewAdded: Boolean = false
    var settings: FloatSetting? = null

    private lateinit var windowManager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    lateinit var applicationContext: Context
    private var floatView: FloatingView? = null

    fun init(context: Context, appSettings: AppSettings) {
        initWindow(context)
        settings = appSettings.getFloatSetting()
    }

    private fun initWindow(context: Context) {
        applicationContext = context.applicationContext
        windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams().apply {

            // 系统全局窗口，可覆盖在任何应用之上，以及单独显示在桌面上
            // 安卓6.0 以后，全局的Window类别，必须使用TYPE_APPLICATION_OVERLAY
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.RGBA_8888
            gravity = Gravity.START or Gravity.TOP
            // 设置浮窗以外的触摸事件可以传递给后面的窗口、不自动获取焦点
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setEnable(floatWindowEnabled: Boolean) {
        if (floatView != null && isFloatingViewAdded) {
            windowManager.removeView(floatView)
        }

        if (floatWindowEnabled) {
            if (floatView == null) {
                floatView = FloatingView(applicationContext).also {
                    it.setOnTouchListener(FloatingOnTouchListener())
                }
            }
            settings?.also { updateSettings(it) }
            params.x = 80
            params.y = 0
            windowManager.addView(floatView, params)
            isFloatingViewAdded = true
        } else {
            isFloatingViewAdded = false
        }
    }

    fun updateSettings(floatSetting: FloatSetting) {
        settings = floatSetting
        floatView?.apply {
            val textColor =
                if (floatSetting.darkMode) context.getColor(R.color.white) else context.getColor(R.color.black)
            val bgRes =
                if (floatSetting.darkMode) R.drawable.text_bg_dark else R.drawable.text_bg_light
            setBackgroundResource(bgRes)
            setTextColor(textColor)
            val fontSize = when (floatSetting.wordFontSize) {
                "small" -> FloatingView.TEXT_SIZE_SMALL
                "normal" -> FloatingView.DEFAULT_TEXT_SIZE
                "large" -> FloatingView.TEXT_SIZE_LARGE
                else -> FloatingView.DEFAULT_TEXT_SIZE
            }
            setFontSize(fontSize)
        }
        if (settings?.draggable == true) {
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        } else {
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        if (floatView != null && isFloatingViewAdded) {
            windowManager.updateViewLayout(floatView, params)
        }
    }

    fun updateWord(word: Word) {
        floatView?.apply {
            setText(word.wordContent, word.toFloatingString())
        }
    }


    inner class FloatingOnTouchListener : View.OnTouchListener {
        private var x = 0
        private var y = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View?, event: MotionEvent): Boolean {
            if (settings?.draggable != true) {
                return false
            }
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
                    params.x = params.x + movedX
                    params.y = params.y + movedY
                    windowManager.updateViewLayout(view, params)
                }
                else -> {}
            }
            return false
        }
    }

    companion object {
        private var instance: FloatingManager? = null
        fun getInstance(): FloatingManager {
            if (instance == null) {
                instance = FloatingManager()
            }
            return instance!!
        }
    }
}
