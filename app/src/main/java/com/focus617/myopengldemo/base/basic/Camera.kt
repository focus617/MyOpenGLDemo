package com.focus617.myopengldemo.base.basic

import android.opengl.Matrix
import com.focus617.myopengldemo.util.Vector

object Camera {
    private const val defaultDistance: Float = 130.0F
    private val WorldUp = Vector(0.0f, 1.0f, 0.0f)

    var Position: Vector= Vector(0.0f, 0.0f, defaultDistance)
    private var directionUp: Vector = WorldUp
    private val targetPos: Vector = Vector(0.0f, 0.0f, 0.0f)

    private lateinit var directionFront: Vector
    private lateinit var directionRight: Vector
    var targetDistance: Float = defaultDistance

    fun rotate(pitch: Float = 0f, yaw: Float = 90f) {
        Position.y = kotlin.math.sin(pitch) * targetDistance
        Position.x = kotlin.math.cos(pitch) * kotlin.math.cos(yaw) * targetDistance
        Position.z = kotlin.math.cos(pitch) * kotlin.math.sin(yaw) * targetDistance

        // also re-calculate the Right and Up vector
        directionFront = Vector(Position, targetPos).normalize()
        directionRight = directionFront.crossProduct(WorldUp).normalize()
        // normalize the vectors, because their length gets closer to 0
        // the more you look up or down which results in slower movement.
        directionUp = directionRight.crossProduct(directionFront).normalize()
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