package com.eight.core.ui.widget.internal

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

class DrawableSpan(
    drawable: Drawable,
    private val paddingStart: Int = 0,
    private val paddingEnd: Int = 0
) : ImageSpan(drawable) {

    override fun getSize(
        paint: Paint, text: CharSequence,
        start: Int, end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val drawable = drawable
        val bounds = drawable.bounds
        if (fm != null) {
            val metrics = paint.fontMetricsInt
            val lineHeight = metrics.bottom - metrics.top
            val drawableHeightHalf = lineHeight.coerceAtLeast(bounds.bottom - bounds.top) / 2
            val centerY = metrics.top + lineHeight / 2

            fm.ascent = centerY - drawableHeightHalf
            fm.descent = centerY + drawableHeightHalf
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }

        return bounds.width() + paddingStart + paddingEnd
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int,
        paint: Paint
    ) {
        val drawable = drawable
        val metrics = paint.fontMetricsInt
        val lineHeight = metrics.descent - metrics.ascent
        val centerY = y + metrics.descent - lineHeight / 2
        val translateY = centerY - drawable.bounds.height() / 2

        canvas.save()

        canvas.translate(x + paddingStart, translateY.toFloat())
        drawable.alpha = Color.alpha(paint.color)
        drawable.draw(canvas)

        canvas.restore()
    }

}