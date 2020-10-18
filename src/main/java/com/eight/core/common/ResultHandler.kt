package com.eight.core.common

import android.content.Intent

interface ResultHandler {
    fun handle(requestCode: Int, resultCode: Int, data: Intent?): Boolean
}