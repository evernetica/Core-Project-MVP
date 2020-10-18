package com.eight.core.extension

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job
import com.eight.core.common.internal.AndroidJob
import com.eight.core.di.module.CoroutineModule
import toothpick.config.Module

fun Module.androidJob(owner: LifecycleOwner): Job = AndroidJob(owner.lifecycle)

inline fun module(crossinline block: Module.() -> Unit) = object : Module() {
    init {
        block()
    }
}

inline fun <T: LifecycleOwner> coroutineModule(
    owner: T,
    crossinline block: Module.(T) -> Unit
) = object : CoroutineModule(owner) {
    init {
        block(owner)
    }
}