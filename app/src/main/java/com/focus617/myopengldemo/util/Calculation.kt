package com.focus617.myopengldemo.util

fun clamp(value: Int, min: Int, max: Int): Int {
    return kotlin.math.max(min, kotlin.math.min(max, value))
}