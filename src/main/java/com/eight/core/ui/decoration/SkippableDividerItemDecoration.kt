package com.eight.core.ui.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.eight.core.ui.ListFragment
import kotlin.math.roundToInt

abstract class SkippableDividerItemDecoration(
    context: Context, orientation: Int
) : RecyclerView.ItemDecoration() {

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL

        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    private var divider: Drawable
    var orientation: Int = 0
        set(value) {
            if (value != HORIZONTAL && value != VERTICAL) error(
                "Invalid orientation. It should be either HORIZONTAL or VERTICAL"
            )

            field = value
        }

    private val bounds = Rect()

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        val d = a.getDrawable(0) ?: error(
            "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration"
        )
        divider = d
        a.recycle()
        this.orientation = orientation
    }

    fun setDrawable(drawable: Drawable?) {
        if (drawable == null) error("Drawable cannot be null.")

        divider = drawable
    }

    protected abstract fun shouldSkip(
        child: View,
        index: Int,
        itemCount: Int,
        parent: RecyclerView
    ): Boolean

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) return

        if (orientation == VERTICAL) drawVertical(c, parent) else drawHorizontal(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        val itemCount = parent.adapter?.itemCount ?: 0

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(child)

            if (shouldSkip(child, index, itemCount, parent)) continue

            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom = bounds.bottom + child.translationY.roundToInt()
            val top = bottom - divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)

        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        val layoutManager = parent.layoutManager!!

        val itemCount = parent.adapter?.itemCount ?: 0

        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )

        } else {
            top = 0
            bottom = parent.height
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(child)

            if (shouldSkip(child, index, itemCount, parent)) continue

            layoutManager.getDecoratedBoundsWithMargins(child, bounds)
            val right = bounds.right + child.translationX.roundToInt()
            val left = right - divider.intrinsicWidth
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)

        }
        canvas.restore()
    }
}

inline fun ListFragment<*>.skippableDivider(
    context: Context, orientation: Int = SkippableDividerItemDecoration.VERTICAL,
    crossinline block: (child: View, index: Int, itemCount: Int, parent: RecyclerView) -> Boolean
) = object : SkippableDividerItemDecoration(context, orientation) {
    override fun shouldSkip(
        child: View,
        index: Int,
        itemCount: Int,
        parent: RecyclerView
    ): Boolean = block(child, index, itemCount, parent)
}