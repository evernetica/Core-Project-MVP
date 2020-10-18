package com.eight.core.presentation

import com.eight.core.presentation.provider.Provider
import com.eight.core.presentation.state.StateProvider

interface Starting {
    fun start()
}

interface Detaching {
    fun detach()
}

interface Refreshing {
    fun refresh()
}

interface Providing<T> {
    val provider: Provider<T>
}

interface Selecting {
    fun selectAt(position: Int)
}

interface StateProviding {
    val stateProvider: StateProvider
}