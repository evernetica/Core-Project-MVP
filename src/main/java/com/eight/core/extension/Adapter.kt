package com.eight.core.extension

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eight.core.ui.ListFragment

@Suppress("FunctionName", "unused")
inline fun <T : Any> ListFragment<*>.itemCallbackOf(
    crossinline areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
    crossinline areContentsTheSame: (T, T) -> Boolean = { old, new -> old.hashCode() == new.hashCode() }
) = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        areItemsTheSame(oldItem, newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        areContentsTheSame(oldItem, newItem)
}

inline fun adapterDataObserver(
    crossinline block: () -> Unit
) = object : RecyclerView.AdapterDataObserver() {
    override fun onChanged() = block()

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = onChanged()
    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) = onChanged()
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = onChanged()
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = onChanged()
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) = onChanged()
}