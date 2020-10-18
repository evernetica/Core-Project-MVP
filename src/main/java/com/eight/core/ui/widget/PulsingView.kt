package com.eight.core.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.eight.core.R

class PulsingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var isAnimating = false
        private set

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val center = PointF()

    private var radius = 0f
    private var waves = FloatArray(0)

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PulsingView, defStyleAttr, 0)

        setWaveCount(ta.getInt(R.styleable.PulsingView_waveCount, 1))
        paint.color = ta.getColor(R.styleable.PulsingView_color, Color.YELLOW)
        paint.style = Paint.Style.values()[ta.getInt(R.styleable.PulsingView_style, 0)]
        paint.strokeWidth = resources.getDimension(R.dimen.stroke)

        ta.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        center.x = w / 2f
        center.y = h / 2f

        radius = w.coerceAtMost(h) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        for (wave in waves) {
            if (wave == 0.0f) continue

            val alpha = (255 * (1.0 - 1.0f * wave)).toInt()
            paint.alpha = alpha
            canvas.drawCircle(center.x, center.y, radius * wave, paint)
        }
    }

    // region Builder
    fun setColor(@ColorInt color: Int) = apply {
        paint.color = color
    }

    fun setStyle(style: Paint.Style) = apply {
        paint.style = style
    }

    fun setWaveCount(waveCount: Int) = apply {
        waves = FloatArray(waveCount) { -(it + 1) / waveCount.toFloat() }
    }

    fun invalidateSelf() = apply {
        invalidate()
    }

    fun start() = apply {
        if (isAnimating) return@apply

        postOnAnimation(animationRunnable)
        postOnAnimation(calculationRunnable)
        isAnimating = true
    }

    fun stop() = apply {
        if (!isAnimating) return@apply

        removeCallbacks(animationRunnable)
        removeCallbacks(calculationRunnable)
        isAnimating = false
    }

    fun toggle() = apply {
        if (isAnimating) stop() else start()
    }
    // endregion

    // region Lifecycle
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)

        if (isVisible) start() else stop()
    }
    // endregion

    // region Internal
    private val calculationRunnable = object : Runnable {
        private var firstIndex = 0
        private val progress = 0.005f
        override fun run() {
            val count = waves.size
            val lastIndex = firstIndex + count

            for (i in firstIndex until lastIndex) {
                val index = i % count
                var wave = waves[index]

                wave += progress
                if (wave >= 1f) {
                    wave = 0f
                    firstIndex++
                    if (firstIndex >= count) firstIndex = 0
                }

                waves[index] = wave
            }

            postOnAnimation(this)
        }
    }

    private val animationRunnable = object : Runnable {
        override fun run() {
            invalidate()
            postOnAnimation(this)
        }
    }
    // endregion
}