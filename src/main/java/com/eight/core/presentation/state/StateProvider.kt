package com.eight.core.presentation.state

import com.eight.core.extension.Delegates

enum class State {
    NONE, LOADING, CONTENT, EMPTY;
}

open class StateProvider {

    interface Observer {
        fun onStateChanged(oldState: State, newState: State)
    }

    var observer: Observer? by Delegates.weak()

    var current: State = State.NONE
        protected set

    open fun push(state: State) {
        val oldState = current
        current = state
        observer?.onStateChanged(oldState, state)
    }
}