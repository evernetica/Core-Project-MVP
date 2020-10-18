package com.eight.core.common.internal

import android.content.res.Resources
import com.eight.core.common.ResourceManager

open class AndroidResourceManager(private val resources: Resources) : ResourceManager {

    override fun stringify(source: Enum<*>): String = source.name

    override fun getString(resId: Int, vararg args: Any): String =
        resources.getString(resId, *args)

    override fun getQuantityString(resId: Int, quantity: Int, vararg args: Any): String =
        resources.getQuantityString(resId, quantity, *args)

    override fun getStringArray(resId: Int): Array<String> = resources.getStringArray(resId)
}