package com.eight.core.common

import kotlinx.coroutines.*

class Debouncer {

    private var job: Job? = null

    operator fun invoke(
        waitMillis: Long,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        block: suspend CoroutineScope.() -> Unit
    ) {
        this.job?.cancel()
        this.job = scope.launch(dispatcher) {
            delay(waitMillis)
            block()
        }
    }

    operator fun invoke(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        block: suspend CoroutineScope.() -> Unit
    ) = invoke(DELAY, scope, dispatcher, block)

    fun cancel() {
        job?.cancel()
        job = null
    }

    private companion object {
        const val DELAY = 150L
    }
}