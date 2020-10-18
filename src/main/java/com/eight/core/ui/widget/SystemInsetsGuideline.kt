package com.eight.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import com.eight.core.R
import com.eight.core.extension.doOnApplyWindowInsets


class SystemInsetsGuideline @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Guideline(context, attrs, defStyleAttr) {

    init {
        requestApplyWindowInsets(context, attrs, defStyleAttr)
    }

    private fun requestApplyWindowInsets(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        if (attrs == null) return

        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.SystemInsetsGuideline, defStyleAttr, 0
        )

        val applyStart = ta.getBoolean(R.styleable.SystemInsetsGuideline_paddingStartSystemWindowInsets, false)
        val applyEnd = ta.getBoolean(R.styleable.SystemInsetsGuideline_paddingEndSystemWindowInsets, false)
        val applyTop = ta.getBoolean(R.styleable.SystemInsetsGuideline_paddingTopSystemWindowInsets, false)
        val applyBottom = ta.getBoolean(R.styleable.SystemInsetsGuideline_paddingBottomSystemWindowInsets, false)

        ta.recycle()

        doOnApplyWindowInsets { view, insets, _ ->
            val start = if (applyStart) insets.systemWindowInsetLeft else 0
            val end = if (applyEnd) insets.systemWindowInsetRight else 0
            val top = if (applyTop) insets.systemWindowInsetTop else 0
            val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

            val params = view.layoutParams as ConstraintLayout.LayoutParams

            if (params.orientation == ConstraintLayout.LayoutParams.VERTICAL) {
                params.guideBegin += start
                params.guideEnd += end
            } else {
                params.guideBegin += top
                params.guideEnd += bottom
            }

            view.layoutParams = params
        }
    }
}