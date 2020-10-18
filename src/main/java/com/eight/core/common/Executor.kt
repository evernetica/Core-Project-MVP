package com.eight.core.common

import kotlinx.coroutines.CoroutineDispatcher

interface Executor {
    val io: CoroutineDispatcher
    val ui: CoroutineDispatcher
    val network: CoroutineDispatcher
}