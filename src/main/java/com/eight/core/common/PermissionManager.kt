package com.eight.core.common

interface PermissionManager {

    val granted: Boolean
    val shouldShowRationale: Boolean

    fun shouldShowRationale(permissions: Array<out String>): BooleanArray
    fun granted(permission: String): Boolean
    fun granted(permissions: Array<out String>): BooleanArray

    suspend fun request(permissions: Array<String>): BooleanArray
    suspend fun request(): Boolean
}