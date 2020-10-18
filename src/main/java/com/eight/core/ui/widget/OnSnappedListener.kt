package com.eight.core.ui.widget

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

abstract class OnSnappedListener(
    val snapHelper: SnapHelper,
    var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL
) : RecyclerView.OnScrollListener() {

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    var snapPosition = RecyclerView.NO_POSITION
        private set

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) maybeNotifySnapPositionChange(recyclerView)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE
            && newState == RecyclerView.SCROLL_STATE_IDLE
        ) maybeNotifySnapPositionChange(recyclerView)
    }

    abstract fun onSnapPositionChanged(position: Int)

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val position = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = snapPosition != position
        if (snapPositionChanged) {
            onSnapPositionChanged(position)
            snapPosition = position
        }
    }

    private fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }
}

@Suppress("FunctionName")
inline fun OnSnappedListener(
    snapHelper: SnapHelper = PagerSnapHelper(),
    behavior: OnSnappedListener.Behavior = OnSnappedListener.Behavior.NOTIFY_ON_SCROLL,
    crossinline onSnapPositionChanged: (position: Int) -> Unit
): OnSnappedListener = object : OnSnappedListener(snapHelper, behavior) {
    override fun onSnapPositionChanged(position: Int) = onSnapPositionChanged(position)
}

fun RecyclerView.attachOnSnappedListener(listener: OnSnappedListener) {
    listener.snapHelper.attachToRecyclerView(this)
    addOnScrollListener(listener)
}