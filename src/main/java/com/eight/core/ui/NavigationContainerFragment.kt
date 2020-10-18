package com.eight.core.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.eight.core.R
import com.eight.core.common.OnBackPressedHelper
import com.eight.core.presentation.router.FlowRouter
import com.eight.core.presentation.router.Scene
import ru.terrakok.cicerone.commands.Command
import toothpick.ktp.delegate.inject

abstract class NavigationContainerFragment : ContainerFragment(),
    FragmentManager.OnBackStackChangedListener {

    override val layoutResId get() = R.layout.fragment_container
    override val fragmentContainerId get() = R.id.container

    protected abstract val initialScreen: Scene

    protected val router: FlowRouter by inject()
    protected val helper: OnBackPressedHelper by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        childFragmentManager.addOnBackStackChangedListener(this)

        if (childFragmentManager.fragments.isEmpty()) router.replaceScreen(initialScreen)
    }

    open fun onBackStackChanged(bar: ActionBar, isBackAllowed: Boolean) {
        bar.setHomeButtonEnabled(isBackAllowed)
        bar.setDisplayHomeAsUpEnabled(isBackAllowed)
    }

    final override fun onBackStackChanged() {
        onBackStackChanged(
            supportActivity?.supportActionBar ?: return,
            !childFragmentManager.isBackStackEmpty
        )
    }

    override fun fragmentTransaction(
        command: Command,
        from: Fragment?,
        to: Fragment?,
        transaction: FragmentTransaction
    ) {
        transaction.setCustomAnimations(
            R.anim.enter, R.anim.exit,
            R.anim.pop_enter, R.anim.pop_exit
        )
    }

    override fun didBecomeActive() {
        if (isHidden) return

        callback.isEnabled = true
        onBackStackChanged()

        currentVisibleFragment?.let {
            configurationCallback.didBecomeActive(childFragmentManager, it, it.requireView())
        }

        supportActivity?.let {
            it.onBackPressedDispatcher.addCallback(this, callback)
            childFragmentManager.addOnBackStackChangedListener(this)
        }

        if (childFragmentManager.fragments.isEmpty()) return
    }

    override fun didResignActive() {
        childFragmentManager.removeOnBackStackChangedListener(this)

        callback.isEnabled = false
        callback.remove()
    }

    override fun onActionBarReady(bar: ActionBar) = Unit

    protected val callback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val activity = activity ?: return
            val isBackStackEmpty = childFragmentManager.isBackStackEmpty
            val skip = activity.isDestroyed || activity.isFinishing

            if (isHidden || skip || (isBackStackEmpty && helper.invokedFromToolbar)) return

            if (isBackStackEmpty) router.finishFlow() else router.exit()
        }
    }
}