package com.eight.core.di.module

import com.eight.core.common.internal.LifecycleAwareNavigationHolder
import com.eight.core.presentation.router.FlowRouter
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import toothpick.Scope
import toothpick.config.Module

class ContainerModule(parentScope: Scope) : Module() {
    init {
        val parentRouter = parentScope.getInstance(Router::class.java)

        val cicerone = Cicerone.create(FlowRouter(parentRouter))
        bind(FlowRouter::class.java).toInstance(cicerone.router)
        bind(LifecycleAwareNavigationHolder::class.java)
            .toInstance(LifecycleAwareNavigationHolder(cicerone.navigatorHolder))
    }
}