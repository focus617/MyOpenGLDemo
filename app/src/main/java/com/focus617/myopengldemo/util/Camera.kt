package com.focus617.myopengldemo.util

import com.focus617.myopengldemo.util.Geometry.Companion.Vector

object Camera {
    var cameraPos: Vector= Vector(0.0f, 0.0f, -5.0f)
    var cameraFront: Vector = Vector(0.0f, 0.0f, 1.0f)
    var cameraUp: Vector = Vector(0.0f, 1.0f, 0.0f)
}