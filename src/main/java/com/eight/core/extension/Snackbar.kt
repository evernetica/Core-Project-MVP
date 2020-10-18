package com.eight.core.extension

import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.DimenRes
import com.google.android.material.snackbar.Snackbar
import com.eight.core.R
import com.eight.core.ui.behavior.DisabledSwipeBehavior

fun Snackbar.disableSwipeDismiss(): Snackbar = setBehavior(DisabledSwipeBehavior())
fun Snackbar.setTextSize(@DimenRes resourceId: Int): Snackbar {
    val textView = view.findViewById<TextView>(R.id.snackbar_text)
    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.resources.getDimension(resourceId))
    return this
}