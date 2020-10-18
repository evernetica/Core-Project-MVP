package com.eight.core.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class DotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var origin = PointF()
    private var radius = 0f

    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        origin.set(w / 2f, h / 2f)
        radius = origin.y
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(origin.x, origin.y, radius, paint)
    }
}