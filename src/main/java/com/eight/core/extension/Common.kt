package com.eight.core.extension

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

fun <T> T.weak() = WeakReference(this)

val Any.className: String get() = javaClass.simpleName
val Any.uniqueName: String get() = className + "@" + hashCode()

val Any.unit: Unit get() = Unit

fun InputMethodManager.hideSoftInputFrom(view: View?) {
    hideSoftInputFromWindow(view?.windowToken ?: return, 0)
}

fun ScrollView.smoothScrollToBottom() {
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    smoothScrollBy(0, delta)
}

fun FragmentActivity.setSoftInputMode(mode: Int) = window.setSoftInputMode(mode)