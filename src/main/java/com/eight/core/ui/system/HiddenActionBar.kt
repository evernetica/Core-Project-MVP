package com.eight.core.ui.system

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.eight.core.ui.ContainerFragment
import com.eight.core.ui.NavigationContainerFragment

@[Target(AnnotationTarget.CLASS) Retention(AnnotationRetention.RUNTIME)]
annotation class HiddenActionBar {

    class Callback : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            if (f is DialogFragment) return

            val activity = f.activity as? AppCompatActivity
            val actionBar = activity?.supportActionBar ?: return
            val showing = find(f) == null

            if (actionBar.isShowing == showing) return

            if (showing) actionBar.show() else actionBar.hide()
        }

        private fun find(from: Fragment): HiddenActionBar? {
            val annotation = annotation(from::class.java)
            val fragment = when (from) {
                is NavigationContainerFragment -> from.currentVisibleFragment
                is ContainerFragment -> from.currentFragment
                else -> from
            } ?: return null

            return annotation(fragment::class.java) ?: annotation
        }

        private fun annotation(from: Class<*>): HiddenActionBar? = from.annotations.find {
            it.annotationClass == HiddenActionBar::class
        } as? HiddenActionBar
    }
}