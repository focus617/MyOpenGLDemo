package com.focus617.myopengldemo.base.basic

import com.focus617.myopengldemo.util.Geometry.Companion.Vector

object Material {
    var ambient: Vector = Vector(1.0f, 0.5f, 0.31f)
    var diffuse: Vector = Vector(1.0f, 0.5f, 0.31f)
    // 镜面强度(Specular Intensity)
    var specular: Vector = Vector(0.5f, 0.5f, 0.5f)

    // 高光的反光度
    var shininess: Float = 100.0f
}
