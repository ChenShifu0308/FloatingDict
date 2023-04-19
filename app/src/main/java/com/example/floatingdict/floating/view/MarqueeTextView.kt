package com.example.floatingdict.floating.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView


class MarqueeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    var screenWidth: Int = 0

    init {
        this.ellipsize = TextUtils.TruncateAt.MARQUEE;
        this.marqueeRepeatLimit = -1;
        this.isSingleLine = true;
        this.maxLines = 1;

        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        screenWidth = metrics.widthPixels
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (screenWidth > 0) {
            val halfWidth = screenWidth * 0.8

            if (measuredWidth < halfWidth) {
                val height = measuredHeight
                setMeasuredDimension(halfWidth.toInt(), height)
            }
        }
    }

    override fun isFocused(): Boolean {
        return true
    }
}
