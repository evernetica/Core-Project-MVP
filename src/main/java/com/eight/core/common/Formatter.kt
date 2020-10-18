package com.eight.core.common

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date as JDate

sealed class Formatter {
    abstract fun format(date: JDate): String
    abstract fun parse(source: String): JDate

    private object Pattern {
        const val TIME = "HH:mm"
        const val DATE = "dd MMMM yyyy"
        const val TIME_DATE = "$TIME, $DATE"
    }

    abstract class WithFormatter(private val df: DateFormat) : Formatter() {
        override fun format(date: JDate): String = df.format(date)
        override fun parse(source: String): JDate = df.parse(source)
    }

    object Time : WithFormatter(SimpleDateFormat(Pattern.TIME, Locale.ENGLISH))
    object Date : WithFormatter(SimpleDateFormat(Pattern.DATE, Locale.ENGLISH))
    object TimeDate : WithFormatter(SimpleDateFormat(Pattern.TIME_DATE, Locale.ENGLISH))

    @Suppress("MemberVisibilityCanBePrivate")
    object Elapsed : Formatter() {

        fun format(millis: Long): String = DateUtils.formatElapsedTime(millis)

        override fun format(date: JDate): String = format(date.time)
        override fun parse(source: String): JDate = JDate()
    }
}