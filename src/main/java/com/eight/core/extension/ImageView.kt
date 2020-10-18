package com.eight.core.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView

fun ImageView.loadAsset(path: String) = setImageDrawable(
    DrawableFactory.createFromAsset(context, path)
)

private object DrawableFactory {
    fun createFromAsset(context: Context, path: String): Drawable? = try {
        Drawable.createFromStream(context.assets.open(path), null)
    } catch (ignore: Exception) {
        null
    }
}