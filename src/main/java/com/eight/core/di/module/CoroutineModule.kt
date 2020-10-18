package com.eight.core.di.module

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job
import com.eight.core.extension.androidJob
import toothpick.config.Module

@Suppress("LeakingThis")
abstract class CoroutineModule(owner: LifecycleOwner) : Module() {
    init {
        bind(Job::class.java).toInstance(androidJob(owner))
    }
}