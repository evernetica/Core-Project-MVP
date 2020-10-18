package com.eight.core.extension

import android.annotation.SuppressLint
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.ViewAnimator
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import kotlin.math.ceil
import kotlin.math.floor

data class ViewPaddingState(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int,
    val start: Int, val end: Int
) {
    @SuppressLint("RtlHardcoded")
    fun relative(to: Int = Gravity.TOP) = when (to) {
        Gravity.START, Gravity.LEFT -> left
        Gravity.END, Gravity.RIGHT -> right
        Gravity.TOP -> top
        Gravity.BOTTOM -> bottom
        else -> 0
    }
}

fun DisplayMetrics.toPixels(dp: Float): Int {
    val pixels = dp * density
    return (if (pixels < 0) ceil(pixels - 0.5f) else floor(pixels + 0.5f)).toInt()
}

fun View.doOnApplyWindowInsets(f: (v: View, insets: WindowInsetsCompat, state: ViewPaddingState) -> Unit) {
    val state = createState(forView = this)

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, state)
        insets
    }

    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() = doOnAttach {
    ViewCompat.requestApplyInsets(it)
}

fun View.doOnAttach(f: (View) -> Unit) {
    if (isAttachedToWindow) {
        f(this)
        return
    }

    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            f(v)
            removeOnAttachStateChangeListener(this)
        }

        override fun onViewDetachedFromWindow(v: View) {
            removeOnAttachStateChangeListener(this)
        }
    })
}

@SuppressLint("RtlHardcoded")
fun WindowInsetsCompat.relative(to: Int = Gravity.TOP) = when (to) {
    Gravity.START, Gravity.LEFT -> systemWindowInsetLeft
    Gravity.END, Gravity.RIGHT -> systemWindowInsetRight
    Gravity.TOP -> systemWindowInsetTop
    Gravity.BOTTOM -> systemWindowInsetBottom
    else -> 0
}

@SuppressLint("RtlHardcoded")
fun View.setPadding(padding: Int, to: Int = Gravity.TOP) = when (to) {
    Gravity.START, Gravity.LEFT -> updatePadding(left = padding)
    Gravity.END, Gravity.RIGHT -> updatePadding(right = padding)
    Gravity.TOP -> updatePadding(top = padding)
    Gravity.BOTTOM -> updatePadding(bottom = padding)
    else -> Unit
}

private fun createState(forView: View) = ViewPaddingState(
    forView.paddingLeft, forView.paddingTop,
    forView.paddingRight, forView.paddingBottom,
    forView.paddingStart, forView.paddingEnd
)