package com.eight.core.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import com.eight.core.common.Executor
import com.eight.core.presentation.Attachable
import kotlin.coroutines.CoroutineContext

abstract class CoroutinePresenter<V: Attachable>(
    view: V, job: Job,
    protected val executor: Executor
) : Presenter<V>(view), CoroutineScope {
    override val coroutineContext: CoroutineContext = executor.ui + job
}