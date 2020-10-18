package com.eight.core.common

interface ExceptionHumanizer {
    fun humanize(throwable: Throwable): String
}