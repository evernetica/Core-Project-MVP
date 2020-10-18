package com.eight.core.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

fun <T> CoroutineScope.catching(
    deferreds: Collection<Deferred<T>>
): Collection<Deferred<Result<T>>> = deferreds.map { deferred ->
    async {
        runCatching { deferred.await() }
    }
}

fun <T> CoroutineScope.catching(
    vararg deferreds: Deferred<T>
): Collection<Deferred<Result<T>>> = deferreds.map { deferred ->
    async {
        runCatching { deferred.await() }
    }
}