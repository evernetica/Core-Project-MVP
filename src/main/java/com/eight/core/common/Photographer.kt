package com.eight.core.common

import java.io.File

interface Photographer {

    enum class Source { CAMERA, GALLERY; }

    suspend fun choose(source: Source): File?

    sealed class Exception(message: String?, cause: Throwable?) : Throwable(message, cause) {
        class ContextNotFound : Exception(null, null)
        class UnableCreateFile : Exception(null, null)
        class UnableLaunchCamera : Exception(null, null)
    }
}