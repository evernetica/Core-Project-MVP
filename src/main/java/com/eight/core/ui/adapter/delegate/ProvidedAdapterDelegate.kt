package com.eight.core.ui.adapter.delegate

import androidx.recyclerview.widget.RecyclerView
import com.eight.core.extension.weak

@Suppress("UNCHECKED_CAST")
abstract class ProvidedAdapterDelegate<in ITEM, LISTENER, in HOLDER>(
    listener: LISTENER? = null
) : AdapterDelegate<ITEM> {

    protected val reference = listener?.weak()

    protected open fun onViewDetachedFromWindow(holder: HOLDER) = Unit

    protected abstract fun onBindViewHolder(position: Int, holder: HOLDER, item: ITEM)
    protected open fun onBindPlaceholder(position: Int) = Unit

    final override fun onBindViewHolder(
        position: Int,
        holder: RecyclerView.ViewHolder,
        item: ITEM?
    ) {
        val h = holder as? HOLDER ?: return
        if (item != null) onBindViewHolder(position, h, item) else onBindPlaceholder(position)
    }

    final override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        onViewDetachedFromWindow(holder as? HOLDER ?: return)
    }

    override fun isForViewType(position: Int, item: ITEM?, payload: Int): Boolean = true
}