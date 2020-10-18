package com.eight.core.common

object Constant {
    const val SPACE = " "
    const val EMPTY = ""
    const val AT = "@"
    const val NEW_LINE = "\n"

    const val SHORT_DURATION = 250L
    const val LONG_DURATION = 500L
}

object MimeType {
    const val TEXT_PLAIN = "text/plain"
}

object Transformation {
    fun insertAtSignIfNeeded(source: String): String = if (source.startsWith(Constant.AT)) source
    else Constant.AT + source

    fun removeAtSignIfNeeded(source: String): String =
        if (source.startsWith(Constant.AT)) source.drop(1)
        else source
}