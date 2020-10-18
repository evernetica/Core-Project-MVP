package com.eight.core.common

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.core.content.edit
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

sealed class Cache {

    abstract fun clear()

    abstract class Files<T>(protected val directory: File) : Cache() {

        protected abstract fun transform(value: T): String
        protected abstract fun transform(source: String): T?

        protected fun save(key: String, value: T) = synchronized(this) {
            val safeKey = safeKey(key)
            val storingFile = File(directory, safeKey)
            val writer = FileWriter(storingFile, false)

            writer.write(transform(value))
            writer.flush()
            writer.close()
        }

        protected fun retrieve(key: String): T? = synchronized(this) {
            return try {
                val safeKey = safeKey(key)
                val storingFile = File(directory, safeKey)

                val reader = BufferedReader(FileReader(storingFile.absoluteFile))
                val value = transform(reader.readText())
                reader.close()

                value

            } catch (ignore: Exception) {
                null
            }
        }

        protected fun deleteBy(key: String) = synchronized(this) {
            val safeKey = safeKey(key)
            val storingFile = File(directory, safeKey)
            storingFile.delete()
        }

        override fun clear() {
            synchronized(this) {
                val files = directory.listFiles()
                files?.forEach { it?.delete() }
            }
        }

        private fun safeKey(key: String): String = key.replace("/", "_")
    }

    abstract class Preferences(context: Context) : Cache() {
        companion object {
            protected const val COMMA = ","

            @JvmStatic
            fun <T: Preferences> shared(context: Context, clazz: Class<T>): SharedPreferences =
                context.getSharedPreferences(clazz.simpleName, Context.MODE_PRIVATE)
        }

        protected val preferences: SharedPreferences = context.getSharedPreferences(
            this::class.java.simpleName, Context.MODE_PRIVATE
        )

        override fun clear() = preferences.edit(commit = true) { clear() }

        // region Delegates
        protected fun SharedPreferences.int(
            defaultValue: Int = 0, key: String? = null, observer: () -> Unit = {}
        ) = delegate(defaultValue, key, SharedPreferences::getInt, Editor::putInt, observer)

        protected fun SharedPreferences.long(
            defaultValue: Long = 0, key: String? = null, observer: () -> Unit = {}
        ) = delegate(defaultValue, key, SharedPreferences::getLong, Editor::putLong, observer)

        protected fun SharedPreferences.optString(
            defaultValue: String? = null, key: String? = null, observer: () -> Unit = {}
        ) = delegate(defaultValue, key, SharedPreferences::getString, Editor::putString, observer)

        protected fun SharedPreferences.string(
            defaultValue: String, key: String? = null, observer: () -> Unit = {}
        ) = delegate(defaultValue, key, SharedPreferences::getString, Editor::putString, observer)

        protected fun SharedPreferences.bool(
            defaultValue: Boolean = false, key: String? = null, observer: () -> Unit = {}
        ) = delegate(defaultValue, key, SharedPreferences::getBoolean, Editor::putBoolean, observer)

        protected fun SharedPreferences.intArray(
            defaultValue: IntArray = intArrayOf(), key: String? = null, observer: () -> Unit = {}
        ) = delegate(
            defaultValue, key,
            { k, d -> getString(k, null)?.split(COMMA)?.map(String::toInt)?.toIntArray() ?: d },
            { k, v -> putString(k, v.joinToString(COMMA)) },
            observer
        )

        private inline fun <T> SharedPreferences.delegate(
            defaultValue: T, key: String?,
            crossinline getter: SharedPreferences.(String, T) -> T,
            crossinline setter: Editor.(String, T) -> Editor,
            crossinline willChange: () -> Unit
        ): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T =
                getter(key ?: property.name, defaultValue)

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                willChange()
                edit().setter(key ?: property.name, value).apply()
            }
        }
        // endregion
    }
}