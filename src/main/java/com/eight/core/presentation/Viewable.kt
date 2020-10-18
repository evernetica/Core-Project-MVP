package com.eight.core.presentation

import androidx.annotation.IntDef

interface Attachable {
    val isAttached: Boolean
}

interface Refreshable {
    var isRefreshing: Boolean
}

interface Statable {
    val stateProviding: StateProviding
}

interface Messageable {

    companion object {
        const val UNDEFINED = -1
        const val TOAST = 0
        const val SNACKBAR = 1

        const val LENGTH_SHORT = -1
        const val LENGTH_LONG = 0
        const val LENGTH_INDEFINITE = -2
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(UNDEFINED, TOAST, SNACKBAR)
    annotation class Type

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LENGTH_SHORT, LENGTH_LONG, LENGTH_INDEFINITE)
    annotation class Duration

    fun message(text: String, @Duration duration: Int = LENGTH_LONG, @Type type: Int = TOAST)
}

@Suppress("unused")
fun Messageable.duration(@Messageable.Duration value: Int, @Messageable.Type of: Int): Int {
    val toast = of != Messageable.SNACKBAR
    return when (value) {
        Messageable.LENGTH_SHORT -> if (toast) 0 else value
        Messageable.LENGTH_LONG -> if (toast) 1 else value
        Messageable.LENGTH_INDEFINITE -> if (toast) 1 else value
        else -> Messageable.LENGTH_LONG
    }
}

interface Clearable {
    fun clearUp()
}