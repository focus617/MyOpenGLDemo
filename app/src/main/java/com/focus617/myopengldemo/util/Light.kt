package com.focus617.myopengldemo.util

import com.focus617.myopengldemo.util.Geometry.Companion.Vector

object Light {
    var position: Vector = Vector(3.0f, 4.0f, 6.0f)

    var ambient: Vector = Vector(0.2f, 0.2f, 0.2f)
    var diffuse: Vector = Vector(0.6f, 0.6f, 0.6f) // 将光照调暗了一些以搭配场景
    var specular: Vector = Vector(1.0f, 1.0f, 1.0f)
}
