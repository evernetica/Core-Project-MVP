package com.eight.core.ui.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Path.Direction
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan

class ImprovedBulletSpan(
    private val bulletRadiusDp: Int,
    private val gapWidthDp: Int,
    private val color: Int
) : LeadingMarginSpan {

    private var bulletPath: Path? = null

    override fun getLeadingMargin(first: Boolean): Int {
        return 2 * bulletRadiusDp + gapWidthDp
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, x: Int, dir: Int,
        top: Int, baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        if (text !is Spanned) return

        if (text.getSpanStart(this) == start) {
            val style = paint.style
            paint.color = color
            paint.style = Paint.Style.FILL

            val yPosition = if (layout != null) {
                val line = layout.getLineForOffset(start)
                layout.getLineBaseline(line).toFloat() - bulletRadiusDp * 2f
            } else {
                (top + bottom) / 2f
            }

            val xPosition = (x + dir * bulletRadiusDp).toFloat()

            if (canvas.isHardwareAccelerated) {
                val bp = bulletPath ?: Path().apply {
                    addCircle(0.0f, 0.0f, bulletRadiusDp.toFloat(), Direction.CW)
                    bulletPath = this
                }

                canvas.save()
                canvas.translate(xPosition, yPosition)
                canvas.drawPath(bp, paint)
                canvas.restore()
            } else {
                canvas.drawCircle(xPosition, yPosition, bulletRadiusDp.toFloat(), paint)
            }

            paint.style = style
        }
    }


}