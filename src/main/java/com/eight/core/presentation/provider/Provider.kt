package com.eight.core.presentation.provider

interface Provider<T> : MutableList<T> {

    interface Delegate {
        fun notifyDataSetChangedAnimated()
        fun notifyItemChanged(position: Int)
        fun notifyItemRangeChanged(positionStart: Int, itemCount: Int)
        fun notifyItemInserted(position: Int)
        fun notifyItemMoved(fromPosition: Int, toPosition: Int)
        fun notifyItemRangeInserted(positionStart: Int, itemCount: Int)
        fun notifyItemRemoved(position: Int)
        fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int)
    }

    var delegate: Delegate?
    fun toList(): List<T>
    fun set(data: List<T>)
}

fun <T> providerOf(): Provider<T> = ProviderImpl()
fun <T> providerOf(vararg items: T): Provider<T> = ProviderImpl<T>().apply { addAll(items) }