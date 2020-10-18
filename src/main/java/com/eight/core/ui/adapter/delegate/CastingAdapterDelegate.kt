package com.eight.core.ui.adapter.delegate

import androidx.recyclerview.widget.RecyclerView
import com.eight.core.extension.weak

@Suppress("UNCHECKED_CAST")
abstract class CastingAdapterDelegate<TYPE, in ITEM, LISTENER, in HOLDER>(
    private val clazz: Class<TYPE>,
    listener: LISTENER? = null
) : AdapterDelegate<ITEM> {

    protected val reference = listener?.weak()

    protected abstract fun onBindViewHolder(position: Int, holder: HOLDER, item: TYPE)
    protected open fun onBindPlaceholder(position: Int) = Unit
    protected open fun onViewDetachedFromWindow(holder: HOLDER) = Unit

    final override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        onViewDetachedFromWindow(holder as? HOLDER ?: return)
    }

    final override fun onBindViewHolder(
        position: Int,
        holder: RecyclerView.ViewHolder,
        item: ITEM?
    ) {
        val h = holder as? HOLDER ?: return
        val casted = item as? TYPE

        if (casted != null) onBindViewHolder(position, h, casted) else onBindPlaceholder(position)
    }

    override fun isForViewType(position: Int, item: ITEM?, payload: Int): Boolean =
        clazz.isInstance(item)
}