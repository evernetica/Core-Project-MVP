package com.eight.core.ui.adapter.delegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class AdapterDelegateManager<in T>(private vararg val delegates: AdapterDelegate<T>) {

    fun getItemViewType(position: Int, item: T, payload: Int) = delegates.indexOfFirst {
        it.isForViewType(position, item, payload)
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = delegates.elementAtOrElse(viewType) {
        error("No AdapterDelegates registered for view type: $viewType")
    }.onCreateViewHolder(parent)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: T?) = delegates
        .elementAtOrNull(holder.itemViewType)
        ?.onBindViewHolder(position, holder, item)
        ?: Unit

    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) = delegates
        .elementAtOrNull(holder.itemViewType)
        ?.onViewDetachedFromWindow(holder)
        ?: Unit
}