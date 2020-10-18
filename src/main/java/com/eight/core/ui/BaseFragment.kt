package com.eight.core.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ScrollingView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eight.core.common.ManagingActiveStatus
import com.eight.core.common.RefreshableController
import com.eight.core.extension.uniqueName
import com.eight.core.presentation.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import toothpick.Scope
import toothpick.ktp.KTP

abstract class BaseFragment : Fragment(), Attachable, Refreshable,
    Messageable, ManagingActiveStatus {

    protected abstract val layoutResId: Int

    protected open val toolbarTitleRes = 0
    protected open val toolbarTitle: CharSequence? get() = getTextIfNeeded(toolbarTitleRes)

    protected open val toolbarSubtitleRes = 0
    protected open val toolbarSubtitle: CharSequence? get() = getTextIfNeeded(toolbarSubtitleRes)

    protected open val optionsMenuRes: Int = 0

    protected val supportActivity: AppCompatActivity? get() = activity as? AppCompatActivity
    protected val holdingAppBarLayout: HoldingAppBarLayout?
        get() = (parentFragment as? HoldingAppBarLayout) ?: this as? HoldingAppBarLayout

    open val refreshLayout: SwipeRefreshLayout?
        get() = (parentFragment as? ContainerFragment)?.refreshLayout
            ?: (activity as? NavigatorActivity)?.refreshLayout

    private val controller: RefreshableController<*> by lazy(::refreshableController)
    override var isRefreshing: Boolean
        get() = controller.isRefreshing
        set(value) {
            controller.isRefreshing = value
        }

    override var isAttached = false
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        val scope = createScope()
        installModules(scope)
        super.onCreate(savedInstanceState)
        scope.inject(this)
        setHasOptionsMenu(optionsMenuRes != 0)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) didResignActive()
        else didBecomeActive()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (optionsMenuRes != 0) inflater.inflate(optionsMenuRes, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAttached = true

        if (this is Statable && this is HoldingStateLayout) stateLayout.attach(stateProviding)
    }

    override fun onDestroyView() {
        isAttached = false
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        if (isHidden) return

        didBecomeActive()
    }

    override fun onStop() {
        super.onStop()
        if (isHidden) return

        didResignActive()
    }

    override fun onDestroy() {
        KTP.closeScope(uniqueName)
        super.onDestroy()
    }

    protected open fun createScope(): Scope {
        val parent = requireNotNull(parentFragment ?: activity).uniqueName
        return KTP.openScopes(parent, uniqueName)
    }

    protected open fun installModules(scope: Scope) = Unit

    open fun onActionBarReady(bar: ActionBar) {
        bar.title = toolbarTitle
        bar.subtitle = toolbarSubtitle
    }

    protected open fun onAppBarLayoutReady(appBarLayout: AppBarLayout) {
        val view = view
        appBarLayout.setLiftable(true)
        if (view is ScrollingView) {
            appBarLayout.liftOnScrollTargetViewId = view.id
            appBarLayout.isLiftOnScroll = true

        } else {
            appBarLayout.liftOnScrollTargetViewId = View.NO_ID
            appBarLayout.isLiftOnScroll = false
        }
    }

    protected open fun refreshableController(): RefreshableController<*> =
        RefreshableController.RefreshLayout(this)

    protected open fun configure(layout: SwipeRefreshLayout) {
        if (this is SwipeRefreshLayout.OnRefreshListener) {
            layout.setOnRefreshListener(this)
            layout.isEnabled = true

        } else {
            layout.setOnRefreshListener(null)
            layout.isEnabled = false
        }
    }

    open fun userInteractionDidChange(enabled: Boolean) = Unit

    // region ManagingActiveStatus
    override fun didBecomeActive() {
        refreshLayout?.let(::configure)

        supportActivity?.supportActionBar?.let(::onActionBarReady)
        holdingAppBarLayout?.appBarLayout?.let(::onAppBarLayoutReady)
    }

    override fun didResignActive() = Unit
    // endregion

    // region Messageable
    override fun message(text: String, duration: Int, @Messageable.Type type: Int) {
        val d = duration(duration, type)

        when (type) {
            Messageable.SNACKBAR -> Snackbar.make(view ?: return, text, d).show()
            Messageable.TOAST,
            Messageable.UNDEFINED -> Toast.makeText(context ?: return, text, d).show()
        }
    }
    // endregion

    private fun getTextIfNeeded(@StringRes resId: Int): CharSequence? =
        if (resId != 0) getText(resId) else null
}