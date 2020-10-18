package com.eight.core.ui.widget

import android.animation.*
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import androidx.annotation.Keep
import androidx.core.graphics.ColorUtils
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.eight.core.R
import com.eight.core.ui.widget.internal.DrawableSpan
import com.google.android.material.button.MaterialButton

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    private var _text: CharSequence? = null
    private var _drawable: Drawable? = null
    private var animations: AnimatorSet? = null

    private var _textColor: Int = Color.WHITE
    private var _textColorList: ColorStateList? = null

    var isLoading: Boolean = false
        set(value) {
            if (field == value && text == _text) return

            field = value
            if (value) show(circularDrawable(context))
            else hideDrawable()
        }

    init {
        _textColor = currentTextColor
        _textColorList = textColors
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        cancelAnimations()
        animations = null

        (_drawable as? Animatable)?.stop()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        (_drawable as? Animatable)?.start()
    }

    // region Setters
    override fun setText(text: CharSequence?, type: BufferType?) {
        if (!settingInternally) _text = text
        if (animations?.isRunning == true && !settingInternally) return

        super.setText(text, type)
    }

    @Keep
    override fun setTextColor(color: Int) {
        if (!settingInternally) _textColor = color
        super.setTextColor(color)
    }

    override fun setTextColor(colors: ColorStateList?) {
        if (!settingInternally) _textColorList = colors
        super.setTextColor(colors)
    }
    // endregion

    // region Internal
    private fun show(drawable: Drawable) {
        (_drawable as? Animatable)?.stop()

        val spannable = drawableSpannable(drawable)
        startFadeInOutAnimation(spannable)

        drawable.callback = callback

        if (drawable is Animatable) drawable.start()

        _drawable = drawable
    }

    private fun hideDrawable() = startFadeInOutAnimation(SpannableString(_text))

    private fun circularDrawable(context: Context) = CircularProgressDrawable(context).apply {
        setStyle(CircularProgressDrawable.DEFAULT)
        setColorSchemeColors(_textColor)
        val size = (centerRadius + strokeWidth).toInt() * 2
        setBounds(0, 0, size, size)
    }

    private fun drawableSpannable(drawable: Drawable): SpannableString {
        val span = DrawableSpan(drawable)
        val spannable = SpannableString(" ")

        spannable.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    private fun resetTextColors() {
        if (animations?.isRunning == false) return

        settingInternally = true
        _textColorList?.let(::setTextColor) ?: setTextColor(_textColor)
        settingInternally = false
    }

    private var settingInternally = false
    private fun setTextInternal(text: CharSequence?) {
        settingInternally = true
        this.text = text
        settingInternally = false
    }

    private val callback = object : Drawable.Callback {
        override fun invalidateDrawable(who: Drawable) = invalidate()
        override fun unscheduleDrawable(who: Drawable, what: Runnable) = Unit
        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) = Unit
    }

    // region Fade-in/out animations

    private fun cancelAnimations() {
        val set = animations ?: return

        set.childAnimations.forEach { it.removeAllListeners() }
        set.cancel()
    }

    private fun startFadeInOutAnimation(toSpannable: SpannableString?) {
        val evaluator = ArgbEvaluator()
        val set = AnimatorSet()

        fadeOutListener.spannableString = toSpannable
        cancelAnimations()
        animations = set

        set.playSequentially(
            ObjectAnimator.ofInt(
                this,
                Property.TEXT_COLOR,
                currentTextColor,
                ColorUtils.setAlphaComponent(_textColor, 0)
            ).setDuration(FADE_DURATION).apply {
                setEvaluator(evaluator)
                addListener(fadeOutListener)
            },

            ObjectAnimator.ofInt(
                this,
                Property.TEXT_COLOR,
                ColorUtils.setAlphaComponent(currentTextColor, 0),
                _textColor
            ).setDuration(FADE_DURATION).apply {
                setEvaluator(evaluator)
                addListener(fadeInListener)
            }
        )

        set.start()
    }

    private val fadeInListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) = resetTextColors()
    }

    private val fadeOutListener = object : AnimatorListenerAdapter() {
        var spannableString: SpannableString? = null
        override fun onAnimationEnd(animation: Animator) {
            setTextInternal(spannableString)
            resetTextColors()
        }
    }

    // endregion

    private object Property {
        const val TEXT_COLOR = "textColor"
    }

    private companion object {
        const val FADE_DURATION = 200L
    }

    // endregion
}