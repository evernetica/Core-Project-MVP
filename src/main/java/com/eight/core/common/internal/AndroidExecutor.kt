package com.eight.core.common.internal

import kotlinx.coroutines.Dispatchers
import com.eight.core.common.Executor

class AndroidExecutor : Executor {
    override val io = Dispatchers.IO
    override val ui = Dispatchers.Main
    override val network = Dispatchers.Default
}