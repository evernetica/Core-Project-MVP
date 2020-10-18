package com.eight.core.ui.widget.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ParallaxPageTransformer(private val parallaxViewId: Int) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> page.alpha = 1f // [-Inf, -1)
            position <= 1 -> { // [-1, 1]
                val width = page.width / 2
                page.findViewById<View>(parallaxViewId)?.translationX = -position * width
            }
            else -> page.alpha = 1f // (1, +Inf]
        }
    }
}