package com.eight.core.extension

import java.util.*

private val calendar by lazy { Calendar.getInstance() }

val Date.daysFromToday: Int
    get() {
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar[Calendar.DAY_OF_YEAR]

        calendar.timeInMillis = time

        return today - calendar[Calendar.DAY_OF_YEAR]
    }

val Date.isToday: Boolean get() = daysFromToday == 0
val Date.isYesterday: Boolean get() = daysFromToday == 1