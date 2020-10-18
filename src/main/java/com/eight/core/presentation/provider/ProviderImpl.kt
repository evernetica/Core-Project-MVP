package com.eight.core.presentation.provider

import com.eight.core.extension.Delegates

internal open class ProviderImpl<T> : Provider<T>, MutableList<T> {

    protected var holder = mutableListOf<T>()

    override var delegate: Provider.Delegate? by Delegates.weak()
    override val size: Int get() = holder.size

    // region Mutable
    override fun add(element: T): Boolean {
        val index = holder.lastIndex
        holder.add(element)
        delegate?.notifyItemInserted(index)
        return true
    }

    override fun remove(element: T): Boolean {
        val index = holder.indexOf(element)
        val removed = holder.remove(element)
        if (removed && index != -1) delegate?.notifyItemRemoved(index)
        return removed
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val lastIndex = holder.lastIndex
        val changed = holder.addAll(elements)
        if (changed) delegate?.notifyItemRangeInserted(lastIndex, elements.size)
        return changed
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val changed = holder.addAll(index, elements)
        if (changed) delegate?.notifyItemRangeInserted(index, elements.size)
        return changed
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val modified = holder.toMutableList()
        val removed = modified.removeAll(elements)
        holder = modified
        delegate?.notifyDataSetChangedAnimated()
        return removed
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val modified = holder.toMutableList()
        val removed = modified.retainAll(elements)
        holder = modified
        delegate?.notifyDataSetChangedAnimated()
        return removed
    }

    override operator fun set(index: Int, element: T): T {
        val previous = holder.set(index, element)
        delegate?.notifyItemChanged(index)
        return previous
    }

    override fun add(index: Int, element: T) {
        holder.add(index, element)
        delegate?.notifyItemInserted(index)
    }

    override fun removeAt(index: Int): T {
        val removed = holder.removeAt(index)
        delegate?.notifyItemRemoved(index)
        return removed
    }

    override fun clear() {
        val count = size
        holder.clear()
        delegate?.notifyItemRangeRemoved(0, count)
    }
    // endregion

    // region List

    override fun contains(element: T): Boolean = holder.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = holder.containsAll(elements)
    override fun get(index: Int): T = holder[index]
    override fun indexOf(element: T): Int = holder.indexOf(element)
    override fun isEmpty(): Boolean = holder.isEmpty()
    override fun iterator(): MutableIterator<T> = holder.iterator()
    override fun lastIndexOf(element: T): Int = holder.lastIndexOf(element)
    override fun listIterator(): MutableListIterator<T> = holder.listIterator()
    override fun listIterator(index: Int): MutableListIterator<T> = holder.listIterator(index)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun subList(from: Int, to: Int): MutableList<T> = holder.subList(from, to)

    // endregion

    override fun set(data: List<T>) {
        val mutable = data as? MutableList<T> ?: data.toMutableList()
        holder = mutable
        delegate?.notifyDataSetChangedAnimated()
    }

    override fun toList(): List<T> = holder
}

