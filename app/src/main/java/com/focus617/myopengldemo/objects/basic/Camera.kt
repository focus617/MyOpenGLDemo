package com.focus617.myopengldemo.objects.basic

import android.opengl.Matrix
import com.focus617.myopengldemo.util.Geometry.Companion.Vector

object Camera {
    private const val defaultDistance: Float = 5.0F

    var Position: Vector= Vector(0.0f, 0.0f, defaultDistance)
    var directionUp: Vector = Vector(0.0f, 1.0f, 0.0f)
    var targetPos: Vector = Vector(0.0f, 0.0f, 0.0f)

    var directionFront: Vector = Vector(Position, targetPos).normalize()
    var targetDistance: Float = defaultDistance

    fun rotate(pitch: Float = 0f, yaw: Float = 0f){
        Position.y = kotlin.math.sin(pitch) * targetDistance
        Position.x = kotlin.math.cos(pitch) * kotlin.math.cos(yaw) * targetDistance
        Position.z = kotlin.math.cos(pitch) * kotlin.math.sin(yaw) * targetDistance
    }

    fun lookAt(): FloatArray {
        val mViewMatrix = FloatArray(16)

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
            Position.x, Position.y, Position.z,
            targetPos.x, targetPos.y, targetPos.z,
            directionUp.x, directionUp.y, directionUp.z)

        return mViewMatrix
    }
}