package com.eight.core.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eight.core.common.internal.LifecycleAwareNavigationHolder
import com.eight.core.common.internal.OnBackPressedHelperImpl
import com.eight.core.di.module.AndroidxActivityModule
import com.eight.core.extension.uniqueName
import com.eight.core.presentation.Attachable
import com.eight.core.presentation.Refreshable
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import toothpick.Scope
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

abstract class NavigatorActivity : AppCompatActivity(), Attachable, Refreshable {

    protected abstract val layoutResId: Int
    protected abstract val navigator: Navigator

    protected open val containerId: Int = 0

    private val holder: NavigatorHolder by inject()
    private val helper: OnBackPressedHelperImpl by inject()

    open val refreshLayout: SwipeRefreshLayout? = null

    override var isRefreshing: Boolean
        get() = refreshLayout?.isRefreshing == true
        set(value) {
            refreshLayout?.isRefreshing = value
        }

    override var isAttached: Boolean = false
        protected set

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            helper.invokedFromToolbar = true
            onBackPressed()
            helper.invokedFromToolbar = false
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createScope()
        installModules(scope)
        super.onCreate(savedInstanceState)
        scope.inject(this)

        isAttached = true
        if (layoutResId != 0) {
            setContentView(layoutResId)
            refreshLayout?.let(::configure)
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        holder.setNavigator(navigator)
    }

    override fun onPause() {
        holder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        isAttached = false
        if (isFinishing) KTP.closeScope(uniqueName)
        super.onDestroy()
    }

    protected open fun configure(layout: SwipeRefreshLayout) {
        if (this is SwipeRefreshLayout.OnRefreshListener) {
            layout.setOnRefreshListener(this)
            layout.isEnabled = true
        } else {
            layout.setOnRefreshListener(null)
            layout.isEnabled = false
        }
    }

    protected open fun createScope(): Scope = KTP.openScopes(application, uniqueName)

    @CallSuper
    protected open fun installModules(scope: Scope) {
        scope.installModules(AndroidxActivityModule(this))
    }

}