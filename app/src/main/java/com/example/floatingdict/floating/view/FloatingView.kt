package com.example.floatingdict.floating.view

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat

class FloatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private var screenWidth: Int = 0
    private var wordTextView: TextView
    private var translationTextView: MarqueeTextView

    init {
        this.orientation = HORIZONTAL
        wordTextView = TextView(context)
        wordTextView.textSize = DEFAULT_TEXT_SIZE

        translationTextView = MarqueeTextView(context)
        translationTextView.text = "  Ready?  "
        translationTextView.textSize = DEFAULT_TEXT_SIZE

        addView(wordTextView)
        addView(translationTextView)

        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        screenWidth = metrics.widthPixels
    }


    fun setFontSize(fontSize: Float) {
        wordTextView.textSize = fontSize
        translationTextView.textSize = fontSize
    }

    fun setTextColor(textColor: Int) {
        wordTextView.setTextColor(textColor)
        translationTextView.setTextColor(textColor)
    }

    fun setText(wordContent: String, toFloatingString: String) {
        wordTextView.text = wordContent
        translationTextView.text = toFloatingString
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

    companion object {
        const val DEFAULT_TEXT_SIZE = 16f

        const val TEXT_SIZE_LARGE = 20f
        const val TEXT_SIZE_SMALL = 13f

    }
}
