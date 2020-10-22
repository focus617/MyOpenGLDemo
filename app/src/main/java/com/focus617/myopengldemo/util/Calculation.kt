package com.focus617.myopengldemo.util

fun clamp(value: Int, min: Int, max: Int): Int {
    return kotlin.math.max(min, kotlin.math.min(max, value))
}

fun clamp(value: Float, min: Float, max: Float): Float {
    return kotlin.math.max(min, kotlin.math.min(max, value))
}

