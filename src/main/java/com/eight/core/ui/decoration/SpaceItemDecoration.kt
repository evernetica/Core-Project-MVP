package com.eight.core.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    var spacing: Int,
    private val beforeFirst: Boolean = false,
    private val afterFirst: Boolean = false,
    private val afterLast: Boolean = false,
    private val beforeLast: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams

                val isAxisEndSpan = (layoutParams.spanIndex + 1) % layoutManager.spanCount == 0
                val isCrossAxisEndSpan = position > (itemCount - layoutManager.spanCount)
                val half = spacing / 2

                when (layoutManager.orientation) {
                    GridLayoutManager.VERTICAL -> with(outRect) {
                        left = if (layoutParams.spanIndex == 0) 0 else half
                        right = if (isAxisEndSpan) 0 else half
                        top = if (position < layoutManager.spanCount && !beforeFirst) 0 else half
                        bottom = if (isCrossAxisEndSpan && !afterLast) 0 else half
                    }

                    GridLayoutManager.HORIZONTAL -> with(outRect) {
                        top = if (layoutParams.spanIndex == 0) 0 else half
                        bottom = if (isAxisEndSpan) 0 else half
                        left = if (position < layoutManager.spanCount && !beforeFirst) 0 else half
                        right = if (isCrossAxisEndSpan && !afterLast) 0 else half
                    }
                }
            }

            is LinearLayoutManager -> {
                val first = position == 0
                val last = position == itemCount - 1
                val vertical = layoutManager.orientation == LinearLayoutManager.VERTICAL

                with(outRect) {
                    if (vertical) {
                        top = if (first && beforeFirst || (last && beforeLast)) spacing else 0
                        bottom = if ((first && afterFirst) || (last && afterLast)) spacing else 0

                    } else {
                        left = if ((first && beforeFirst) || (last && beforeLast)) spacing else 0
                        right = if ((first && afterFirst) || (last && afterLast)) spacing else 0
                    }
                }
            }
        }

    }
}