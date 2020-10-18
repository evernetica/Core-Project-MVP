package com.eight.core.ui.text

import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape

class ShapeTextDrawable(
    shape: Shape,
    color: Int,
    backgroundColor: Int,
    private val letters: String
) : ShapeDrawable() {

    enum class Shape { SQUARE, ROUND }

    private val tp = Paint(Paint.ANTI_ALIAS_FLAG) // text paint

    init {
        paint.color = backgroundColor

        tp.style = Paint.Style.FILL
        tp.color = color
        tp.textAlign = Paint.Align.CENTER

        this.shape = when (shape) {
            Shape.SQUARE -> RectShape()
            Shape.ROUND -> OvalShape()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val bounds = bounds

        val count = canvas.save()

        canvas.drawText(
            letters,
            bounds.width() / 2f,
            bounds.height() / 2f - (tp.descent() + tp.ascent()) / 2,
            tp
        )

        canvas.restoreToCount(count)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bounds ?: return

        tp.textSize = bounds.width().coerceAtMost(bounds.height()) / 2.5f
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun getIntrinsicHeight(): Int = -1
    override fun getIntrinsicWidth(): Int = -1
}