package com.eight.core.ui.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderDecoration(
    private val delegate: Delegate,
    private val renderInline: Boolean
) : RecyclerView.ItemDecoration() {

    interface Delegate {
        fun getId(position: Int): Long
        fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
        fun onBindHeaderViewHolder(position: Int, holder: RecyclerView.ViewHolder)
    }

    private val cache = hashMapOf<Long, RecyclerView.ViewHolder>()

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        var headerHeight = 0

        if (position != RecyclerView.NO_POSITION
            && hasHeader(position)
            && showHeaderAbove(position)
        ) {

            val header = getHeader(parent, position).itemView
            headerHeight = getHeaderHeightForLayout(header)
        }

        outRect.set(0, headerHeight, 0, 0)
    }

    private fun showHeaderAbove(position: Int) = if (position != 0) {
        val previous = position - 1
        delegate.getId(previous) != delegate.getId(position)
    } else true

    /**
     * Clears the header view cache. Headers will be recreated and
     * rebound on list scroll after this method has been called.
     */
    fun clearCache() = cache.clear()

    private fun hasHeader(position: Int) = delegate.getId(position) != RecyclerView.NO_ID

    private fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder {
        val key = delegate.getId(position)

        return if (!cache.containsKey(key)) {
            val holder = delegate.onCreateHeaderViewHolder(parent)
            val header = holder.itemView

            delegate.onBindHeaderViewHolder(position, holder)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredWidth, View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredHeight, View.MeasureSpec.UNSPECIFIED
            )

            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec, parent.paddingLeft + parent.paddingRight, header.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec, parent.paddingTop + parent.paddingBottom, header.layoutParams.height
            )

            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)

            cache[key] = holder

            holder

        } else cache[key]!!
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.childCount
        var previousHeaderId: Long = -1

        for (layoutPos in 0 until count) {
            val child = parent.getChildAt(layoutPos)
            val adapterPos = parent.getChildAdapterPosition(child)

            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                val headerId = delegate.getId(adapterPos)

                if (headerId != previousHeaderId) {
                    previousHeaderId = headerId
                    val header = getHeader(parent, adapterPos).itemView
                    canvas.save()

                    val left = child.left
                    val top = getHeaderTop(parent, child, header, adapterPos, layoutPos)
                    canvas.translate(left.toFloat(), top.toFloat())

                    header.translationX = left.toFloat()
                    header.translationY = top.toFloat()
                    header.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun getHeaderTop(
        parent: RecyclerView, child: View, header: View,
        adapterPos: Int, layoutPos: Int
    ): Int {
        val headerHeight = getHeaderHeightForLayout(header)
        var top = child.y.toInt() - headerHeight
        if (layoutPos == 0) {
            val count = parent.childCount
            val currentId = delegate.getId(adapterPos)
            // find next view with header and compute the offscreen push if needed
            for (i in 1 until count) {
                val adapterPosition = parent.getChildAdapterPosition(parent.getChildAt(i))
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val nextId = delegate.getId(adapterPosition)
                    if (nextId != currentId) {
                        val next = parent.getChildAt(i)
                        val offset = next.y.toInt() - (headerHeight + getHeader(
                            parent,
                            adapterPosition
                        ).itemView.height)

                        if (offset < 0) {
                            return offset
                        } else {
                            break
                        }
                    }
                }
            }
            top = Math.max(0, top)
        }

        return top
    }

    private fun getHeaderHeightForLayout(header: View) = if (renderInline) 0 else header.height
}