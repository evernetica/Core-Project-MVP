package com.eight.core.presentation.router

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.eight.core.R
import com.eight.core.ui.system.ConfigurationCallback
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace

open class FlowableAppNavigator(
    private val activity: FragmentActivity,
    private val manager: FragmentManager,
    private val containerId: Int,
    private val callback: ConfigurationCallback?,
    private val enterAnim: Int = R.anim.enter,
    private val exitAnim: Int = R.anim.exit,
    private val popEnterAnim: Int = R.anim.pop_enter,
    private val popExitAnim: Int = R.anim.pop_exit
) : SupportAppNavigator(activity, manager, containerId) {

    override fun applyCommand(command: Command?) {
        val activity = activity
        if (activity.isFinishing || activity.isDestroyed) return

        if (command !is Replace) {
            super.applyCommand(command)
            return
        }

        val scene = command.screen as Scene
        val targetFragment = manager.findFragmentByTag(scene.screenKey) ?: createFragment(scene)

        with(manager.beginTransaction()) {
            setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)

            val fragments = manager.fragments
            for (fragment in fragments) {
                if (fragment == targetFragment) continue
                hide(fragment)
            }

            if (targetFragment.isAdded) {
                show(targetFragment)
                val view = targetFragment.view
                if (callback != null && view != null) {
                    callback.didBecomeActive(manager, targetFragment, view)
                }

            } else add(containerId, targetFragment, scene.screenKey)

            onScreenReplaced(scene)

            commit()
        }
    }

    protected open fun onScreenReplaced(screen: SupportAppScreen) = Unit
}