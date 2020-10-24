package com.focus617.myopengldemo.base.basic

import com.focus617.myopengldemo.utils.Vector

object ObjectMaterial {
    var ambient: Vector = Vector(1.0f, 0.5f, 0.31f)
    var diffuse: Vector = Vector(1.0f, 0.5f, 0.31f)
    // 镜面强度(Specular Intensity)
    var specular: Vector = Vector(0.5f, 0.5f, 0.5f)

    // 高光的反光度
    var shininess: Float = 100.0f
}

class Material(
    val name: String
){
    var ambient: Vector = Vector(0.763f, 0.657f, 0.614f)
    var diffuse: Vector = Vector(0.763f, 0.657f, 0.614f)
    // 镜面强度(Specular Intensity)
    var specular: Vector = Vector(0.5f, 0.5f, 0.5f)

    // 高光的反光度
    var shininess: Float = 100.0f

    var textureDiffuse: Int = 0
    var textureSpecular: Int = 0
}
