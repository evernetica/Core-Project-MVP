package com.eight.core.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.eight.core.R
import com.eight.core.common.Constant

@Deprecated(Constant.EMPTY, replaceWith = ReplaceWith("PulsingView"))
class RippleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    companion object {
        private const val STROKE = 1
        private const val FILL = 0
    }

    private lateinit var ripplePaint: Paint
    private lateinit var rippleBounds: RectF
    private lateinit var animatorSet: AnimatorSet
    private var radius = 0F
    private var _rippleColor = 0
    var rippleColor
        get() = _rippleColor
        set(value) {
            _rippleColor = value
            invalidatePaint()
        }

    private var _pulseType = STROKE
    var pulseType
        get() = _pulseType
        set(value) {
            _pulseType = value
            invalidatePaint()
        }

    private var _rippleStrokeWidth = 0F
    var rippleStrokeWidth
        get() = _rippleStrokeWidth
        set(value) {
            _rippleStrokeWidth = value
            invalidatePaint()
        }

    private var _pulseDuration = 0
    var pulseDuration
        get() = _pulseDuration
        set(value) {
            _pulseDuration = value
            restartPulse()
        }

    private var _startDelay = 0
    var startDelay
        get() = _startDelay
        set(value) {
            _startDelay = value
            restartPulse()
        }

    private var _endDelay = 0
    var endDelay
        get() = _endDelay
        set(value) {
            _endDelay = value
            restartPulse()
        }

    private var _rippleStartRadiusPercent = 0F
    var rippleStartRadiusPercent
        get() = _rippleStartRadiusPercent
        set(value) {
            _rippleStartRadiusPercent = value
            restartPulse()
        }

    private var _rippleEndRadiusPercent = 150F
    var rippleEndRadiusPercent
        get() = _rippleEndRadiusPercent
        set(value) {
            _rippleEndRadiusPercent = value
            restartPulse()
        }

    private var _pulseInterpolator = android.R.anim.decelerate_interpolator
    var pulseInterpolator
        get() = _pulseInterpolator
        set(value) {
            _pulseInterpolator = value
            restartPulse()
        }

    private var _showPreview = false
    var showPreview
        get() = _showPreview
        set(value) {
            _showPreview = value
            restartPulse()
        }

    init {
        _rippleColor = ContextCompat.getColor(context, R.color.design_default_color_background)
        _pulseDuration = resources.getInteger(android.R.integer.config_longAnimTime)
        _rippleStrokeWidth = resources.getDimension(R.dimen.rippleStrokeWidth)
        setWillNotDraw(false)
        initAttributes(attrs, defStyle)
    }

    private fun initAttributes(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RippleView, defStyle, 0)

        val tv = TypedValue()

        _rippleColor = a.getColor(R.styleable.RippleView_ripple_color, rippleColor)
        _rippleStrokeWidth =
            a.obtain(tv, R.styleable.RippleView_ripple_stroke_width)?.float ?: rippleStrokeWidth
        _pulseDuration = a.obtain(tv, R.styleable.RippleView_pulse_duration)?.data ?: pulseDuration
        _startDelay = a.obtain(tv, R.styleable.RippleView_start_delay)?.data ?: startDelay
        _endDelay = a.obtain(tv, R.styleable.RippleView_end_delay)?.data ?: endDelay
        _pulseType = a.obtain(tv, R.styleable.RippleView_pulse_type)?.data ?: pulseType
        _rippleStartRadiusPercent =
            a.obtain(tv, R.styleable.RippleView_ripple_start_radius_percent)?.float
                ?: rippleStartRadiusPercent
        _rippleEndRadiusPercent =
            a.obtain(tv, R.styleable.RippleView_ripple_end_radius_percent)?.float
                ?: rippleEndRadiusPercent
        _pulseInterpolator =
            a.obtain(tv, R.styleable.RippleView_pulse_interpolator)?.resourceId ?: pulseInterpolator

        if (isInEditMode) _showPreview =
            a.getBoolean(R.styleable.RippleView_show_preview, showPreview)

        a.recycle()

        ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rippleBounds = RectF()
        animatorSet = AnimatorSet()

        invalidatePaint()
    }

    private fun restartPulse() {
        stopPulse()
        startPulse()
    }

    fun startPulse() {
        if (!isAnimationRunning()) startAnimator()
    }

    fun stopPulse() {
        animatorSet.removeAllListeners()
        animatorSet.cancel()
        invalidate()
    }

    fun isAnimationRunning() = animatorSet.isRunning

    private fun startAnimator() {
        val scale = ValueAnimator.ofFloat(rippleStartRadiusPercent, rippleEndRadiusPercent).apply {
            addUpdateListener {
                radius = it.animatedValue as Float
                if (radius > 0) {
                    invalidate(rippleBounds, radius)
                    invalidate()
                }
            }
        }

        val alpha = ValueAnimator.ofInt(255, 0).apply {
            addUpdateListener {
                val alpha = it.animatedValue as Int
                ripplePaint.alpha = alpha
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    ripplePaint.alpha = 255
                }
            })
        }

        animatorSet.apply {
            duration = pulseDuration.toLong()
            startDelay = this@RippleView.startDelay.toLong()
            interpolator = AnimationUtils.loadInterpolator(context, pulseInterpolator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    postDelayed({
                        if (!isAnimationRunning()) {
                            invalidate(rippleBounds, rippleStartRadiusPercent)
                            animatorSet.start()
                        }
                    }, endDelay.toLong())
                }
            })
            playTogether(scale, alpha)
        }
        animatorSet.start()
    }

    private fun invalidatePaint() {
        ripplePaint.apply {
            color = rippleColor
            strokeWidth = if (pulseType == STROKE) rippleStrokeWidth else 0F
            style = if (pulseType == FILL) Paint.Style.FILL else Paint.Style.STROKE
        }
    }

    private fun invalidate(bounds: RectF, percent: Float) {
        val halfWidth = (width / 2)
        val halfHeight = (height / 2)
        bounds.apply {
            left = halfWidth - (halfWidth * (percent / 100.0f))
            top = halfHeight - (halfHeight * (percent / 100.0f))
            right = halfWidth + (halfWidth * (percent / 100.0f))
            bottom = halfHeight + (halfHeight * (percent / 100.0f))
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopPulse()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (isInEditMode && showPreview) invalidate(rippleBounds, rippleStartRadiusPercent)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        (parent as? ViewGroup)?.apply {
            clipChildren = false
            if (!isInLayout) requestLayout()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isAnimationRunning() || isInEditMode) {
            drawPulse(canvas, rippleBounds, ripplePaint)
            if (isInEditMode && showPreview) {
                for (idx in rippleStartRadiusPercent.toInt()..rippleEndRadiusPercent.toInt() step 50) {
                    val paint =
                        Paint(ripplePaint).apply { alpha = if (pulseType == FILL) 50 else 100 }
                    val bounds = RectF(rippleBounds).apply { invalidate(this, idx.toFloat()) }
                    drawPulse(canvas, bounds, paint)
                }
            }
        }
    }

    private fun TypedArray.obtain(typedValue: TypedValue, styleId: Int) =
        if (getValue(styleId, typedValue)) typedValue else null

    private fun drawPulse(canvas: Canvas?, bounds: RectF, paint: Paint) {
        canvas?.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2, paint)
    }
}