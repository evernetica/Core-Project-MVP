package com.eight.core.common

interface ResourceManager {
    fun stringify(source: Enum<*>): String
    fun getString(resId: Int, vararg args: Any): String
    fun getQuantityString(resId: Int, quantity: Int, vararg args: Any): String
    fun getStringArray(resId: Int): Array<String>
}