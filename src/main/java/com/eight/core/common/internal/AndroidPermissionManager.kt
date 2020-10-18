package com.eight.core.common.internal

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.eight.core.common.PermissionManager
import com.eight.core.extension.weak
import java.lang.ref.WeakReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Need to add following code to [AppCompatActivity.onActivityResult]:
 * ```
 * val delegate = ActivityCompat.getPermissionCompatDelegate()
 * if (delegate is ActivityCompat.OnRequestPermissionsResultCallback) {
 *     delegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
 * }
 * ```
 */
class AndroidPermissionManager(
    activity: AppCompatActivity,
    private val permissions: Array<String>
) : PermissionManager, LifecycleObserver,
    ActivityCompat.PermissionCompatDelegate,
    ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        private const val REQUEST_CODE = 0xABC
    }

    private val reference: WeakReference<AppCompatActivity> = activity.weak()
    private var continuation: Continuation<BooleanArray>? = null

    constructor(
        fragment: Fragment,
        permissions: Array<String>
    ) : this(fragment.activity as AppCompatActivity, permissions)

    init {
        activity.lifecycle.addObserver(this)
        ActivityCompat.setPermissionCompatDelegate(this)
    }

    override val granted: Boolean get() = granted(permissions).all { it }
    override val shouldShowRationale: Boolean get() = shouldShowRationale(permissions).all { it }

    override fun shouldShowRationale(permissions: Array<out String>): BooleanArray {
        val activity = reference.get() ?: return BooleanArray(permissions.size)
        return BooleanArray(permissions.size) { index ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[index])
        }
    }

    override fun granted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            reference.get() ?: return false,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun granted(permissions: Array<out String>): BooleanArray {
        val activity = reference.get() ?: return BooleanArray(permissions.size)

        return BooleanArray(permissions.size) { index ->
            ActivityCompat.checkSelfPermission(
                activity,
                permissions[index]
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override suspend fun request(permissions: Array<String>): BooleanArray = suspendCoroutine { c ->
        val activity = reference.get()

        if (activity == null) {
            c.resume(BooleanArray(permissions.size))
            return@suspendCoroutine
        }

        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
        continuation = c
    }

    override suspend fun request(): Boolean = request(permissions).all { it }

    override fun requestPermissions(
        activity: Activity, permissions: Array<out String>, requestCode: Int
    ): Boolean = false

    override fun onActivityResult(
        activity: Activity, requestCode: Int, resultCode: Int, data: Intent?
    ): Boolean = requestCode == REQUEST_CODE

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) continuation?.resume(
            BooleanArray(permissions.size) { grantResults[it] == PackageManager.PERMISSION_GRANTED }
        )

        continuation = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        ActivityCompat.setPermissionCompatDelegate(null)
        continuation?.resumeWithException(IllegalStateException())
        continuation = null
    }
}