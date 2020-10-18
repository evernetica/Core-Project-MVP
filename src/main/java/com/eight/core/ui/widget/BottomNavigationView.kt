package com.eight.core.ui.widget

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.core.view.children
import com.eight.core.R
import com.eight.core.common.Constant
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationView : BottomNavigationView {

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private var interpolator: Interpolator? = null
    private var itemWidth = 0f
    private var duration = Constant.SHORT_DURATION

    var isAnimating = false
        private set

    private var _selectedItemId = super.getSelectedItemId()

    private lateinit var highlightView: View

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!changed) return

        itemWidth = right / menu.size().toFloat()
        onNavigationItemSelectedAt(
            menu.children.indexOfFirst { it.itemId == selectedItemId },
            false
        )
    }

    fun setSelectedItemIdSilently(itemId: Int) {
        if (_selectedItemId == itemId) return

        super.setOnNavigationItemSelectedListener(null)
        _selectedItemId = itemId
        selectedItemId = itemId
        super.setOnNavigationItemSelectedListener(proxyListener)
    }

    override fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        if (listener == proxyListener) super.setOnNavigationItemSelectedListener(listener)
        else proxyListener.listener = listener
    }

    override fun setSelectedItemId(itemId: Int) {
        _selectedItemId = itemId
        super.setSelectedItemId(itemId)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        setOnNavigationItemSelectedListener(proxyListener)

        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.BottomNavigationView, defStyleAttr, 0
        )

        val w = ta.getDimensionPixelOffset(R.styleable.BottomNavigationView_highlightWidth, 110)
        val h = ta.getDimensionPixelOffset(R.styleable.BottomNavigationView_highlightHeight, 100)
        val background = ta.getResourceId(R.styleable.BottomNavigationView_highlightBackground, 0)
        val interpolatorId = ta.getResourceId(
            R.styleable.BottomNavigationView_highlightInterpolator,
            android.R.anim.overshoot_interpolator
        )
        duration = ta.getInteger(R.styleable.BottomNavigationView_transitionDuration, 250).toLong()

        interpolator = try {
            AnimationUtils.loadInterpolator(context, interpolatorId)
        } catch (ignore: Exception) {
            null
        }

        ta.recycle()

        highlightView = View(context).apply {
            setBackgroundResource(background)
            layoutParams = LayoutParams(w, h, Gravity.CENTER_VERTICAL)
        }

        addView(highlightView, 0)
    }

    private fun onNavigationItemSelectedAt(position: Int, animated: Boolean) {
        if (isAnimating && position == animationListener.position) return

        val translateX = position * itemWidth + itemWidth / 2 - highlightView.width / 2
        animationListener.position = position

        if (animated) {
            highlightView
                .animate()
                .translationX(translateX)
                .setDuration(duration)
                .setListener(animationListener)
                .setInterpolator(interpolator)
                .start()

        } else highlightView.translationX = translateX
    }

    private fun Menu.indexOf(item: MenuItem): Int = children.indexOf(item)

    private val animationListener = object : Animator.AnimatorListener {
        var position = 0
        override fun onAnimationStart(animation: Animator?) {
            isAnimating = true
        }

        override fun onAnimationEnd(animation: Animator?) {
            isAnimating = false
        }

        override fun onAnimationRepeat(animation: Animator?) = onAnimationStart(animation)
        override fun onAnimationCancel(animation: Animator?) = onAnimationEnd(animation)
    }

    private val proxyListener = object : OnNavigationItemSelectedListener {
        var listener: OnNavigationItemSelectedListener? = null
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val position = menu.indexOf(item)
            onNavigationItemSelectedAt(position, true)
            _selectedItemId = item.itemId
            listener?.onNavigationItemSelected(item)
            return true
        }
    }
}