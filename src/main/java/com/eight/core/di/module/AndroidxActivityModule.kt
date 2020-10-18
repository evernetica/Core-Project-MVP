package com.eight.core.di.module

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.eight.core.common.OnBackPressedHelper
import com.eight.core.common.internal.OnBackPressedHelperImpl
import toothpick.config.Module
import javax.inject.Provider

@Suppress("LeakingThis")
open class AndroidxActivityModule(activity: AppCompatActivity) : Module() {
    init {
        val helper = OnBackPressedHelperImpl()

        bind(OnBackPressedHelper::class.java).toInstance(helper)
        bind(OnBackPressedHelperImpl::class.java).toInstance(helper)

        bind(Activity::class.java).toInstance(activity)
        bind(FragmentManager::class.java)
            .toProviderInstance(AndroidxFragmentManagerProvider(activity))

        bind(LayoutInflater::class.java).toProviderInstance(LayoutInflaterProvider(activity))
    }
}

internal class AndroidxFragmentManagerProvider(
    private val activity: Activity
) : Provider<FragmentManager> {
    override fun get(): FragmentManager = (activity as FragmentActivity).supportFragmentManager
}

internal class LayoutInflaterProvider(
    private val activity: Activity
) : Provider<LayoutInflater> {
    override fun get(): LayoutInflater = activity.layoutInflater
}