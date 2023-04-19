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
    init {
        this.ellipsize = TextUtils.TruncateAt.MARQUEE;
        this.marqueeRepeatLimit = -1;
        this.isSingleLine = true;
        this.maxLines = 1;

    }

    override fun isFocused(): Boolean {
        return true
    }
}
