package com.eight.core.common

import android.content.Context
import java.nio.charset.Charset

object AssetsLoader {

    fun readFile(context: Context, filename: String) = context.assets.open(filename).use {
        it.readBytes().toString(Charset.defaultCharset())
    }

}