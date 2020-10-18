package com.eight.core.presentation.presenter

import com.eight.core.extension.weak
import com.eight.core.presentation.Attachable

abstract class Presenter<V : Attachable>(view: V) {
    protected val reference = view.weak()
    protected val view: V? get() = reference.get()?.takeIf(Attachable::isAttached)
}