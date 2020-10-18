package com.eight.core.extension

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Delegates {
    fun <T> weak(): ReadWriteProperty<Any, T?> = WeakVar(null)
    fun <T> weak(value: T): ReadWriteProperty<Any, T?> = WeakVar(value)
}

internal class WeakVar<T>(value: T?) : ReadWriteProperty<Any, T?> {
    private var reference: WeakReference<T>? = value?.weak()

    override fun getValue(thisRef: Any, property: KProperty<*>): T? = reference?.get()
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        reference = value?.weak()
    }
}