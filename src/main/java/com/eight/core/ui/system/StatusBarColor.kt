package com.eight.core.ui.system

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.eight.core.common.has
import com.eight.core.ui.ContainerFragment


/** Changes statusBar to opposite when fragment's view is created */
@[Target(AnnotationTarget.CLASS) Retention(AnnotationRetention.RUNTIME)]
annotation class StatusBarColor(val color: Int, val useLightIcons: Boolean = false) {

    companion object {
        fun setLightness(decorView: View, light: Boolean) {
            val systemUiVisibility = decorView.systemUiVisibility

            if (systemUiVisibility has SYSTEM_UI_FLAG_LIGHT_STATUS_BAR == light) return

            decorView.systemUiVisibility =
                if (light) systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    sealed class Callback : FragmentManager.FragmentLifecycleCallbacks() {

        companion object {
            @[SuppressLint("ObsoleteSdkInt") JvmStatic]
            fun create(): Callback = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) Stub()
            else Working()
        }

        protected class State(
            val useLightStatusBar: Boolean,
            val statusBarColor: Int
        )

        protected var state = State(true, Color.TRANSPARENT)

        open fun with(activity: Activity) {
            val window = activity.window
            state = State(
                window.decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR == SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                window.statusBarColor
            )
        }

        private class Stub : Callback() {
            override fun with(activity: Activity) = Unit
        }

        private class Working : Callback() {
            override fun onFragmentViewCreated(
                fm: FragmentManager, f: Fragment,
                v: View, savedInstanceState: Bundle?
            ) {
                val window = f.activity?.window
                val decorView = window?.decorView ?: return

                val annotation = findAnnotation(f)

                if (annotation == null) {
                    if (f is DialogFragment) return
                    setLightness(decorView, state.useLightStatusBar)
                    window.statusBarColor = state.statusBarColor
                    return
                }

                val color = annotation.color
                window.statusBarColor = color
                setLightness(
                    decorView,
                    if (color == Color.TRANSPARENT) !annotation.useLightIcons
                    else !color.isDarkColor()
                )
            }

            private fun findAnnotation(from: Fragment?): StatusBarColor? {
                if (from == null) return null
                val annotation = annotation(from::class.java)

                return annotation ?: findAnnotation(
                    if (from is ContainerFragment) null else from.parentFragment
                )
            }

            private fun annotation(from: Class<*>): StatusBarColor? = from.annotations.find {
                it.annotationClass == StatusBarColor::class
            } as? StatusBarColor

            private fun Int.isDarkColor(): Boolean = ColorUtils.calculateLuminance(this) < 0.25
        }
    }
}