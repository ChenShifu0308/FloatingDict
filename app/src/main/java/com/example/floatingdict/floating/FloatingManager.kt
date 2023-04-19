package com.example.floatingdict.floating

import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.floatingdict.R
import com.example.floatingdict.data.model.FloatSetting
import com.example.floatingdict.data.model.Word
import com.example.floatingdict.floating.view.MarqueeTextView
import com.example.floatingdict.settings.AppSettings

class FloatingManager {
    private var isFloatingViewAdded: Boolean = false
    var settings: FloatSetting? = null

    private lateinit var windowManager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    lateinit var frameLayout: FrameLayout
    lateinit var applicationContext: Context
    var marqueeTextView: MarqueeTextView? = null

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
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    fun setEnable(floatWindowEnabled: Boolean) {
        if (marqueeTextView != null && isFloatingViewAdded) {
            windowManager.removeView(marqueeTextView)
        }

        if (floatWindowEnabled) {
            if(marqueeTextView==null) {
                marqueeTextView = MarqueeTextView(applicationContext)
            }
            settings?.also { updateSettings(it) }
            marqueeTextView?.text = " Ready? XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
            windowManager.addView(marqueeTextView, params)
            isFloatingViewAdded = true
        } else {
            isFloatingViewAdded = false
        }
    }

    fun updateSettings(floatSetting: FloatSetting) {
        marqueeTextView?.apply {
            val textColor =
                if (floatSetting.darkMode) context.getColor(R.color.white) else context.getColor(R.color.black)
            val bgColor =
                if (floatSetting.darkMode) context.getColor(R.color.black) else context.getColor(R.color.white)
            setTextColor(textColor)
            setBackgroundColor(bgColor)
        }
    }

    fun updateWord(word: Word) {
        marqueeTextView?.apply {
            text = word.toFloatingString()
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
