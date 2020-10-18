@file:Suppress("NOTHING_TO_INLINE")

package com.eight.core.common

inline infix fun Int.has(flag: Int) = flag and this == flag
inline infix fun Int.without(flag: Int) = this and flag.inv()
