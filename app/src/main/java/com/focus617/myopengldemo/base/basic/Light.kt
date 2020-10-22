package com.focus617.myopengldemo.base.basic

import com.focus617.myopengldemo.util.Vector

// 点光源
object PointLight {
    var position: Vector = Vector(3.0f, 4.0f, -6.0f)

    var ambient: Vector = Vector(0.2f, 0.2f, 0.2f)
    var diffuse: Vector = Vector(1.0f, 1.0f, 1.0f)
    var specular: Vector = Vector(1.0f, 1.0f, 1.0f)

    const val Constant: Float = 1.0f
    const val Linear: Float = 0.09f
    const val Quadratic: Float = 0.032f
}

// 聚光
object SpotLight {
    var position: Vector = Vector(3.0f, 4.0f, -6.0f)
    var direction: Vector = Vector(-3.0f, -4.0f, 6.0f)
    var cutOff: Float = 12.5f

    var ambient: Vector = Vector(0.2f, 0.2f, 0.2f)
    var diffuse: Vector = Vector(1.0f, 1.0f, 1.0f)
    var specular: Vector = Vector(1.0f, 1.0f, 1.0f)

    const val Constant: Float = 1.0f
    const val Linear: Float = 0.09f
    const val Quadratic: Float = 0.032f
}

// 平行光
object DirectionalLight {
    var direction: Vector = Vector(-3.0f, -4.0f, 6.0f)

    var ambient: Vector = Vector(0.2f, 0.2f, 0.2f)
    var diffuse: Vector = Vector(1.0f, 1.0f, 1.0f)
    var specular: Vector = Vector(1.0f, 1.0f, 1.0f)
}
