package com.focus617.myopengldemo.data

import android.opengl.GLES31.*
import com.focus617.myopengldemo.util.Geometry.Circle
import com.focus617.myopengldemo.util.Geometry.Cylinder

class ObjectBuilder(private val sizeInVertices: Int) {

    private val vertexArray = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    private val drawList: MutableList<DrawCommand> = ArrayList()

    private var offset = 0

    fun appendCircle(circle: Circle, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfCircleInVertices(numPoints)

        // Center point of fan
        vertexArray[offset++] = circle.center.x
        vertexArray[offset++] = circle.center.y
        vertexArray[offset++] = circle.center.z

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (i in 0..numPoints) {
            val angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())

            vertexArray[offset++] =
                circle.center.x + (circle.radius * kotlin.math.cos(angleInRadians))
            vertexArray[offset++] = circle.center.y
            vertexArray[offset++] =
                circle.center.z + (circle.radius * kotlin.math.sin(angleInRadians))
        }
        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })
    }

    fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfOpenCylinderInVertices(numPoints)
        val yStart: Float = cylinder.center.y - cylinder.height / 2f
        val yEnd: Float = cylinder.center.y + cylinder.height / 2f

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (i in 0..numPoints) {
            val angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())

            val xPosition: Float =
                cylinder.center.x + (cylinder.radius * kotlin.math.cos(angleInRadians))
            val zPosition: Float =
                cylinder.center.z + (cylinder.radius * kotlin.math.sin(angleInRadians))

            vertexArray[offset++] = xPosition
            vertexArray[offset++] = yStart
            vertexArray[offset++] = zPosition
            vertexArray[offset++] = xPosition
            vertexArray[offset++] = yEnd
            vertexArray[offset++] = zPosition
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        })
    }

    fun buildData(): GeneratedData {
        return GeneratedData(vertexArray, sizeInVertices, drawList)
    }

    companion object {
        private const val FLOATS_PER_VERTEX = 3

        interface DrawCommand {
            fun draw()
        }

        class GeneratedData(
            val vertexArray: FloatArray,    // 顶点数组
            val vertexNumber: Int,          // 顶点数量
            val drawList: List<DrawCommand> // 绘制方法
        )

        /**
         * 计算圆柱体顶部的顶点数量
         *
         * 说明：一个圆柱体的顶部是一个用三角形扇构造的圆；它有一个顶点在圆心，围着圆的每个
         *      点都有一个顶点， 并且围着圆的第一个顶点要重复两次才能使圆闭合。
         */
        fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }

        /**
         * 计算圆柱体侧面的顶点数量
         *
         * 说明：一个圆柱体的侧面是一个卷起来的长方形，由一个三角形带构造， 围着顶部圆的每个
         * 点都需要两个顶点（对应上下的圆面），并且前两个顶点要重复两次才能使这个管闭合。
         */
        fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }
    }
}



