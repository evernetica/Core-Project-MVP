package com.eight.core.presentation.presenter

import com.eight.core.common.ExceptionHumanizer
import com.eight.core.common.Executor
import com.eight.core.presentation.Attachable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class ExceptionHandlingPresenter<V : Attachable>(
    view: V, job: Job, executor: Executor,
    protected val humanizer: ExceptionHumanizer
) : CoroutinePresenter<V>(view, job, executor) {
    abstract val handler: CoroutineExceptionHandler
    override val coroutineContext: CoroutineContext by lazy { super.coroutineContext + handler }
}