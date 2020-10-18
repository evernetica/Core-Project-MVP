package com.eight.core.common

import android.util.Patterns
import java.util.regex.Pattern as RegexPattern

sealed class Validation {
    sealed class Field(private vararg val validations: Validation) : Validation() {
        object Email : Field(Blank, Pattern(Patterns.EMAIL_ADDRESS))
        object Phone : Field(Blank, Pattern(Patterns.PHONE))
        object Name : Field(Blank, LengthGreaterThan())

        override fun validate(value: String): Boolean = validations.all { it.validate(value) }

        operator fun invoke(value: String): Result<String> {
            val results = validations.map { it.validate(value, this) }
            val exception = results.firstOrNull { it.isFailure }

            @Suppress("IfThenToElvis")
            return if (exception != null) exception else results.first { it.isSuccess }
        }
    }

    object Blank : Validation() {
        override fun validate(value: String): Boolean = value.isNotBlank()
    }

    class Pattern(private val pattern: RegexPattern) : Validation() {
        override fun validate(value: String): Boolean = pattern.matcher(value).matches()
    }

    class LengthGreaterThan(private val min: Int = DEFAULT_VALUE) : Validation() {
        override fun validate(value: String): Boolean = value.length > min

        private companion object {
            const val DEFAULT_VALUE = 2
        }
    }

    abstract class Custom : Validation()

    class Exception(val field: Field, val validation: Validation) : kotlin.Exception()

    protected fun validate(value: String, field: Field): Result<String> =
        if (validate(value)) Result.success(value)
        else Result.failure(Exception(field, this))

    abstract fun validate(value: String): Boolean
}