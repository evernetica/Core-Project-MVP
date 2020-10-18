package com.eight.core.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eight.core.presentation.provider.Provider
import com.eight.core.ui.adapter.delegate.AdapterDelegate
import com.eight.core.ui.adapter.delegate.AdapterDelegateManager
import java.lang.ref.WeakReference

class RecyclerAdapter<ITEM>(
    itemCallback: DiffUtil.ItemCallback<ITEM>,
    private val provider: Provider<ITEM>,
    vararg delegates: AdapterDelegate<ITEM>
) : ListAdapter<ITEM, RecyclerView.ViewHolder>(itemCallback), Provider.Delegate {

    private val manager = AdapterDelegateManager(*delegates)

    init {
        provider.delegate = this
        submitList(provider.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        manager.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        manager.onBindViewHolder(holder, position, getItem(position))

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) =
        manager.onViewDetachedFromWindow(holder)

    override fun getItemViewType(position: Int): Int {
        val item = provider[position] ?: return 0
        return manager.getItemViewType(position, item, 0)
    }

    override fun notifyDataSetChangedAnimated() {
        submitList(provider.toList())
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun unsubscribe(): RecyclerAdapter<ITEM> {
        provider.delegate = null
        return this
    }

    @Suppress("LeakingThis", "CanBeParameter", "MemberVisibilityCanBePrivate")
    abstract class VH<LISTENER : VH.Listener>(
        parent: ViewGroup,
        protected val listener: WeakReference<out LISTENER>?,
        @LayoutRes layoutRes: Int,
        clickable: Boolean = false
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    ) {

        interface Listener {
            fun onViewHolderClick(holder: RecyclerView.ViewHolder, position: Int, id: Int)
        }

        val context: Context get() = itemView.context

        init {
            if (clickable && listener != null) {
                itemView.setOnClickListener(createListener(listener))
            }
        }

        protected open fun createListener(l: WeakReference<out LISTENER>) = View.OnClickListener {
            l.get()?.onViewHolderClick(this, adapterPosition, it.id)
        }
    }
}