package com.eight.core.ui.adapter.delegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface AdapterDelegate<in T> {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)
    fun onBindViewHolder(position: Int, holder: RecyclerView.ViewHolder, item: T?)
    fun isForViewType(position: Int, item: T?, payload: Int): Boolean
}