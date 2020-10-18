package com.eight.core.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eight.core.R
import com.eight.core.ui.adapter.RecyclerAdapter
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_list.view.*

abstract class ListFragment<A> : BaseFragment(), RecyclerAdapter.VH.Listener
    where A : RecyclerAdapter<*> {

    override val layoutResId = R.layout.fragment_list

    protected lateinit var adapter: A
    protected open val recyclerView: RecyclerView? get() = view?.recyclerView
    protected open val requiredRecyclerView: RecyclerView get() = requireView().recyclerView

    protected abstract fun createAdapter(): A

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = createAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configure(requireContext(), requiredRecyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        holdingAppBarLayout?.appBarLayout?.setLifted(false)
    }

    protected open fun configure(context: Context, recyclerView: RecyclerView) {
        val layoutManager = createLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        createDividerItemDecoration(context)?.let(recyclerView::addItemDecoration)
    }

    override fun onAppBarLayoutReady(appBarLayout: AppBarLayout) {
        appBarLayout.setLiftable(true)
        appBarLayout.liftOnScrollTargetViewId = requiredRecyclerView.id
        appBarLayout.isLiftOnScroll = true
    }

    protected open fun createDividerItemDecoration(context: Context): RecyclerView.ItemDecoration? {
        //ContextCompat.getDrawable(context, R.drawable.divider)?.let(decoration::setDrawable)
        return DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    }

    protected open fun createLayoutManager(context: Context): RecyclerView.LayoutManager =
        LinearLayoutManager(context)
}