@file:Suppress("FunctionName")

package com.eight.core.common

import com.eight.core.BuildConfig
import com.eight.core.presentation.Messageable
import com.eight.core.presentation.Refreshable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class ExceptionHandler : AbstractCoroutineContextElement(CoroutineExceptionHandler),
    CoroutineExceptionHandler {

    final override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler.Key
}

inline fun ExceptionHandler(
    crossinline f: (context: CoroutineContext, exception: Throwable) -> Unit
): CoroutineExceptionHandler = object : ExceptionHandler() {
    override fun handleException(context: CoroutineContext, exception: Throwable) =
        f(context, exception)
}

inline fun <V> DefaultExceptionHandler(
    humanizer: ExceptionHumanizer,
    crossinline getter: () -> V?,
    crossinline block: (view: V, exception: Throwable) -> Unit = { _, _ -> }
): CoroutineExceptionHandler where V : Messageable, V : Refreshable = object : ExceptionHandler() {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val view = getter() ?: return

        if (BuildConfig.DEBUG) exception.printStackTrace()

        view.isRefreshing = false
        view.message(humanizer.humanize(exception))
        block(view, exception)
    }
}