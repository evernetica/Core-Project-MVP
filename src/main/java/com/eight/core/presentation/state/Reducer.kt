package com.eight.core.presentation.state

import com.eight.core.presentation.Providing
import com.eight.core.presentation.provider.providerOf

class Reducer<T> : StateProvider(), Providing<T> {

    override val provider = providerOf<T>()

    fun push(content: List<T>) {
        provider.set(content)
        push(if (content.isEmpty()) State.EMPTY else State.CONTENT)
    }
}