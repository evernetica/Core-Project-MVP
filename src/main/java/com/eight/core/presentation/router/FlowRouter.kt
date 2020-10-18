package com.eight.core.presentation.router

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppScreen
import javax.inject.Inject

class FlowRouter @Inject constructor(private val router: Router) : Router() {
    fun newRootFlow(screen: SupportAppScreen) = router.newRootScreen(screen)
    fun startFlow(screen: SupportAppScreen) = router.navigateTo(screen)
    fun replaceFlow(screen: SupportAppScreen) = router.replaceScreen(screen)
    fun finishFlow() = router.exit()
}