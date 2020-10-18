package com.eight.core.ui.system

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

class ConfigurationCallback(
    private val fitting: FittingSystemWindows.Callback,
    private val statusBar: StatusBarColor.Callback,
    private val toolbarVisibility: HiddenActionBar.Callback
) : Application.ActivityLifecycleCallbacks {

    fun didBecomeActive(fm: FragmentManager, f: Fragment, view: View) {
        fitting.onFragmentViewCreated(fm, f, view, null)
        statusBar.onFragmentViewCreated(fm, f, view, null)
        toolbarVisibility.onFragmentViewCreated(fm, f, view, null)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity !is FragmentActivity) return

        activity.supportFragmentManager.apply {
            registerFragmentLifecycleCallbacks(fitting, true)
            registerFragmentLifecycleCallbacks(toolbarVisibility, true)

            statusBar.with(activity)
            registerFragmentLifecycleCallbacks(statusBar, true)
        }
    }

    override fun onActivityPaused(activity: Activity?) = Unit
    override fun onActivityResumed(activity: Activity?) = Unit
    override fun onActivityStarted(activity: Activity?) = Unit
    override fun onActivityDestroyed(activity: Activity?) = Unit
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) = Unit
    override fun onActivityStopped(activity: Activity?) = Unit
}