package com.eight.core.common.internal

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder

class LifecycleAwareNavigationHolder(
    private val holder: NavigatorHolder
) : LifecycleObserver {

    private var navigator: Navigator? = null

    fun setNavigator(navigator: Navigator) {
        this.navigator = navigator
    }

    fun register(owner: LifecycleOwner) = owner.lifecycle.addObserver(this)

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumed() = holder.setNavigator(navigator)

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun paused() = holder.removeNavigator()
}