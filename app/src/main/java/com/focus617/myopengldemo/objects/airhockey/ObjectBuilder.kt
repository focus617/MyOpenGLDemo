package com.focus617.myopengldemo.objects.airhockey

import android.opengl.GLES31.*
import com.focus617.myopengldemo.util.Geometry.*

class ObjectBuilder private constructor(sizeInVertices: Int) {

    private val vertexData = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    private var offset = 0

    private val drawList: MutableList<DrawCommand> = ArrayList()

    private fun appendCircle(circle: Circle, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfCircleInVertices(numPoints)

        // Center point of fan
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (i in 0..numPoints) {
            val angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())

            vertexData[offset++] =
                circle.center.x + (circle.radius * kotlin.math.cos(angleInRadians))
            vertexData[offset++] = circle.center.y
            vertexData[offset++] =
                circle.center.z + (circle.radius * kotlin.math.sin(angleInRadians))
        }
        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })
    }

    private fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {
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

            vertexData[offset++] = xPosition
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition
            vertexData[offset++] = xPosition
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        })
    }

    private fun build(): GeneratedData {
        return GeneratedData(vertexData, drawList)
    }

    companion object {
        private const val FLOATS_PER_VERTEX = 3

        interface DrawCommand {
            fun draw()
        }

        class GeneratedData(val vertexData: FloatArray, val drawList: List<DrawCommand>)

        /**
         * 生成一个冰球
         */
        fun createPuck(
            puck: Cylinder,
            numPoints: Int
        ): GeneratedData {

            val size =
                (sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints))

            val builder = ObjectBuilder(size)

            val puckTop = Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius
            )

            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCylinder(puck, numPoints)

            return builder.build()
        }

        /**
         * 生成一个木槌
         */
        fun createMallet(
            center: Point,
            radius: Float,
            height: Float,
            numPoints: Int
        ): GeneratedData {

            val size =
                (sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2)

            val builder = ObjectBuilder(size)

            // First, generate the mallet base.
            val baseHeight = height * 0.25f

            val baseCircle = Circle(
                center.translateY(-baseHeight), radius
            )
            val baseCylinder = Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight
            )

            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCylinder(baseCylinder, numPoints)

            // Now generate the mallet handle.
            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f

            val handleCircle = Circle(
                center.translateY(height * 0.5f), handleRadius
            )
            val handleCylinder = Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius, handleHeight
            )

            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCylinder(handleCylinder, numPoints)

            return builder.build()
        }

        /**
         * 计算圆柱体顶部的顶点数量
         *
         * 说明：一个圆柱体的顶部是一个用三角形扇构造的圆；它有一个顶点在圆心，围着圆的每个
         *      点都有一个顶点， 并且围着圆的第一个顶点要重复两次才能使圆闭合。
         */
        private fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }

        /**
         * 计算圆柱体侧面的顶点数量
         *
         * 说明：一个圆柱体的侧面是一个卷起来的长方形，由一个三角形带构造， 围着顶部圆的每个
         * 点都需要两个顶点（对应上下的圆面），并且前两个顶点要重复两次才能使这个管闭合。
         */
        private fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }


    }

}
