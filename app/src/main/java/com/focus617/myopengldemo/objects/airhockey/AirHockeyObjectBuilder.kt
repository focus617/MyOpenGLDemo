package com.focus617.myopengldemo.objects.airhockey

import com.focus617.myopengldemo.data.ObjectBuilder
import com.focus617.myopengldemo.data.ObjectBuilder.Companion.GeneratedData
import com.focus617.myopengldemo.util.Geometry

object AirHockeyObjectBuilder {
    /**
     * 生成一个冰球
     */
    fun createPuck(puck: Geometry.Cylinder, numPoints: Int): GeneratedData {

        val size = (
                ObjectBuilder.sizeOfCircleInVertices(numPoints)
                        + ObjectBuilder.sizeOfOpenCylinderInVertices(numPoints)
                )

        val builder = ObjectBuilder(size)

        val puckTop = Geometry.Circle(
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
        center: Geometry.Point,
        radius: Float,
        height: Float,
        numPoints: Int
    ): GeneratedData {

        val size = (
                ObjectBuilder.sizeOfCircleInVertices(numPoints) * 2
                        + ObjectBuilder.sizeOfOpenCylinderInVertices(numPoints) * 2
                )

        val builder = ObjectBuilder(size)

        // First, generate the mallet base.
        val baseHeight = height * 0.25f

        val baseCircle = Geometry.Circle(
            center.translateY(-baseHeight), radius
        )
        val baseCylinder = Geometry.Cylinder(
            baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight
        )

        builder.appendCircle(baseCircle, numPoints)
        builder.appendOpenCylinder(baseCylinder, numPoints)

        // Now generate the mallet handle.
        val handleHeight = height * 0.75f
        val handleRadius = radius / 3f

        val handleCircle = Geometry.Circle(
            center.translateY(height * 0.5f), handleRadius
        )
        val handleCylinder = Geometry.Cylinder(
            handleCircle.center.translateY(-handleHeight / 2f),
            handleRadius, handleHeight
        )

        builder.appendCircle(handleCircle, numPoints)
        builder.appendOpenCylinder(handleCylinder, numPoints)

        return builder.build()
    }

}