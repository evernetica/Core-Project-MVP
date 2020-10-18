package com.eight.core.ui.behavior

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar

class DisabledSwipeBehavior : BaseTransientBottomBar.Behavior() {
    override fun canSwipeDismissView(child: View) = false
}