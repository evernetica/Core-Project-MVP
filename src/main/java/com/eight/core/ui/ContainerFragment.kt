package com.eight.core.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eight.core.common.internal.LifecycleAwareNavigationHolder
import com.eight.core.di.module.ContainerModule
import com.eight.core.ui.system.ConfigurationCallback
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import toothpick.Scope
import toothpick.ktp.delegate.inject

abstract class ContainerFragment : BaseFragment() {

    protected abstract val fragmentContainerId: Int

    val currentFragment: BaseFragment?
        get() {
            if (!isAttached) return null
            return childFragmentManager.findFragmentById(fragmentContainerId) as? BaseFragment
        }

    val currentVisibleFragment: BaseFragment?
        get() {
            if (!isAttached) return null
            return childFragmentManager.fragments.firstOrNull(Fragment::isVisible) as? BaseFragment
        }

    private val holder: LifecycleAwareNavigationHolder by inject()
    protected val configurationCallback: ConfigurationCallback by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        holder.register(this)
        holder.setNavigator(navigator)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        val manager = childFragmentManager
        val fragment = currentFragment ?: return

        if (!hidden) configurationCallback.didBecomeActive(manager, fragment, fragment.requireView())

        with(manager.beginTransaction()) {
            if (hidden) hide(fragment) else show(fragment)
            commit()
        }
    }

    override fun configure(layout: SwipeRefreshLayout) = Unit

    @CallSuper
    override fun installModules(scope: Scope) {
        scope.installModules(ContainerModule(scope))
    }

    protected open fun onExit() = Unit
    protected open fun fragmentTransaction(
        command: Command,
        from: Fragment?,
        to: Fragment?,
        transaction: FragmentTransaction
    ) = Unit

    protected val FragmentManager.isBackStackEmpty: Boolean get() = backStackEntryCount == 0

    protected open val navigator: Navigator by lazy {
        object : SupportAppNavigator(activity, childFragmentManager, fragmentContainerId) {
            override fun activityBack() = onExit()
            override fun setupFragmentTransaction(
                command: Command,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction
            ) {
                fragmentTransaction.setReorderingAllowed(true)
                fragmentTransaction(command, currentFragment, nextFragment, fragmentTransaction)
            }
        }
    }
}