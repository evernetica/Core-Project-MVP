package com.eight.core.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eight.core.R
import com.eight.core.common.Constant
import kotlinx.android.synthetic.main.view_station_nearby.view.*

class StationNearbyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val interpolator = FastOutSlowInInterpolator()

    init {
        View.inflate(context, R.layout.view_station_nearby, this)

        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.StationNearbyView, defStyleAttr, 0
        )

        textView.text = ta.getText(R.styleable.StationNearbyView_text)
        textView.setTextAppearance(
            ta.getResourceId(
                R.styleable.StationNearbyView_textStyle,
                R.style.TextAppearance_AppCompat_Body1
            )
        )
        textView.setTextColor(
            ta.getColor(
                R.styleable.StationNearbyView_textColor,
                Color.WHITE
            )
        )

        pulsingView
            .setWaveCount(ta.getInt(R.styleable.StationNearbyView_waves, 1))
            .setColor(ta.getColor(R.styleable.StationNearbyView_waveColor, Color.WHITE))
            .setStyle(Paint.Style.values()[ta.getInt(R.styleable.StationNearbyView_waveStyle, 0)])

        ta.recycle()
    }

    fun hide() {
        if (!isVisible) return

        animate()
            .withEndAction(actionGone)
            .translationY(height.toFloat())
            .setInterpolator(interpolator)
            .setDuration(Constant.LONG_DURATION)
            .alpha(0f)
            .start()
    }

    fun show() {
        if (isVisible) return
        if (translationY == 0f) {
            alpha = 0f
            translationY = measuredHeight.toFloat()
        }

        animate()
            .withStartAction(actionVisible)
            .translationY(0f)
            .setInterpolator(interpolator)
            .setDuration(Constant.LONG_DURATION)
            .alpha(1f)
            .start()
    }

    private val actionVisible = Runnable {
        isVisible = true
    }

    private val actionGone = Runnable {
        isVisible = false
    }

}