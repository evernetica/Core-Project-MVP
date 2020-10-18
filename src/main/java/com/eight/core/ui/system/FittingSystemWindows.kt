package com.eight.core.ui.system

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.eight.core.extension.doOnApplyWindowInsets
import com.eight.core.extension.relative
import com.eight.core.extension.setPadding

/** Pairs of View Id and Gravity to apply [androidx.core.view.WindowInsetsCompat] values */
@[Target(AnnotationTarget.CLASS) Retention(AnnotationRetention.RUNTIME)]
annotation class FittingSystemWindows(
    val target: IntArray = [],
    val gravity: IntArray = []
) {
    class Callback : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager, f: Fragment,
            v: View, savedInstanceState: Bundle?
        ) {
            val annotation = annotation(f::class.java) ?: run {
                ViewCompat.requestApplyInsets(v)
                return
            }

            v.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            ViewCompat.requestApplyInsets(v)

            if (annotation.target.size != annotation.gravity.size || annotation.target.isEmpty()) return

            v.doOnApplyWindowInsets { view, insets, _ ->

                for ((index, id) in annotation.target.withIndex()) {
                    val gravity = annotation.gravity[index]
                    val target = view.findViewById<View>(id) ?: continue

                    val params = target.layoutParams
                    if (params is ViewGroup.MarginLayoutParams) {
                        params.setMargin(insets.relative(gravity), gravity)
                        target.layoutParams = params

                    } else target.setPadding(insets.relative(gravity), gravity)

                }

            }
        }

        private fun annotation(from: Class<*>): FittingSystemWindows? = from.annotations.find {
            it.annotationClass == FittingSystemWindows::class
        } as? FittingSystemWindows

        @SuppressLint("RtlHardcoded")
        private fun ViewGroup.MarginLayoutParams.setMargin(margin: Int, toGravity: Int) =
            when (toGravity) {
                Gravity.START, Gravity.LEFT -> marginStart = margin
                Gravity.END, Gravity.RIGHT -> marginEnd = margin
                Gravity.TOP -> topMargin = margin
                Gravity.BOTTOM -> bottomMargin = margin
                else -> Unit
            }
    }
}