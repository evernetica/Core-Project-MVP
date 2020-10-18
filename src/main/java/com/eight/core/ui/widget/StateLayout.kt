package com.eight.core.ui.widget

import android.animation.LayoutTransition
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.eight.core.R
import com.eight.core.presentation.StateProviding
import com.eight.core.presentation.state.State
import com.eight.core.presentation.state.StateProvider

class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var state: State = State.NONE
        set(value) {
            field = value
            updateViewsVisibility()
        }

    init {
        layoutTransition = LayoutTransition()
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StateLayout, 0, 0)

        try {
            state = ta.getInt(R.styleable.StateLayout_state, 0).let(State.values()::get)

        } finally {
            ta.recycle()
        }
    }

    fun attach(toStateProvider: StateProvider) {
        toStateProvider.observer = observer
    }

    fun attach(toStateProviding: StateProviding) {
        toStateProviding.stateProvider.observer = observer
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        val layoutParams = params as LayoutParams

        if (layoutParams.state != State.NONE) {
            super.addView(child, index, params)
            updateVisibilityFor(child)
        }
    }

    override fun generateDefaultLayoutParams() = LayoutParams(context, null)
    override fun generateLayoutParams(attrs: AttributeSet?) = LayoutParams(context, attrs)

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.ordinal = state.ordinal
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state as SavedState
        super.onRestoreInstanceState(state.superState)

        this.state = State.values()[state.ordinal]
    }

    private fun updateViewsVisibility() {
        var child: View
        for (i in 0 until childCount) {
            child = getChildAt(i)

            updateVisibilityFor(child)
        }
    }

    private fun updateVisibilityFor(view: View) {
        val state = state
        val params = view.layoutParams as LayoutParams
        val visible = state == params.state

        view.isVisible = visible
    }

    private val observer = object : StateProvider.Observer {
        override fun onStateChanged(oldState: State, newState: State) {
            if (oldState == newState) return

            state = newState
        }
    }

    class LayoutParams(context: Context, attrs: AttributeSet?) :
        FrameLayout.LayoutParams(context, attrs) {

        val state: State

        init {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.StateLayout_Layout, 0, 0)

            state = ta.getInt(R.styleable.StateLayout_Layout_layout_state, 0)
                .let(State.values()::get)

            ta.recycle()
        }
    }

    private class SavedState : BaseSavedState {
        constructor(parcelable: Parcelable?) : super(parcelable)
        constructor(parcel: Parcel) : super(parcel) {
            ordinal = parcel.readInt()
        }

        var ordinal = 0
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(ordinal)
        }
    }
}