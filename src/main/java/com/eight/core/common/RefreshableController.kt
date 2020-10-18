package com.eight.core.common

import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eight.core.extension.weak
import com.eight.core.presentation.Refreshable
import com.eight.core.ui.BaseFragment
import com.eight.core.ui.widget.ProgressButton

sealed class RefreshableController<T : BaseFragment>(owner: T) : Refreshable {

    class RefreshLayout<T : BaseFragment>(owner: T) : RefreshableController<T>(owner) {
        override var isRefreshing: Boolean
            get() = owner?.refreshLayout?.isRefreshing == true
            set(value) {
                val owner = owner ?: return
                val layout = owner.refreshLayout ?: return

                layout.isEnabled = !value && owner is SwipeRefreshLayout
                layout.isRefreshing = value

                owner.userInteractionDidChange(!value)
            }
    }

    class Button<T : BaseFragment>(
        owner: T,
        private inline val getter: (T) -> ProgressButton?
    ) : RefreshableController<T>(owner) {
        override var isRefreshing: Boolean
            get() = owner?.let(getter)?.isLoading == true
            set(value) {
                val owner = owner ?: return
                val button = getter(owner) ?: return

                button.isLoading = value

                owner.userInteractionDidChange(!value)
            }
    }

    class ProgressBar<T : BaseFragment>(
        owner: T,
        private inline val getter: (T) -> android.widget.ProgressBar?
    ) : RefreshableController<T>(owner) {
        override var isRefreshing: Boolean
            get() = owner?.let(getter)?.isVisible == true
            set(value) {
                val owner = owner ?: return
                val progressBar = getter(owner) ?: return

                progressBar.isVisible = value

                owner.userInteractionDidChange(!value)
            }
    }

    protected val reference = owner.weak()
    protected open val owner get() = reference.get()
}