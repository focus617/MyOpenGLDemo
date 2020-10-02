package com.focus617.myopengldemo.objects.airhockey

import com.focus617.myopengldemo.data.VertexArrayEs2
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.Geometry.Cylinder
import com.focus617.myopengldemo.objects.airhockey.ObjectBuilder.Companion.DrawCommand
import com.focus617.myopengldemo.objects.airhockey.ObjectBuilder.Companion.GeneratedData
import com.focus617.myopengldemo.programs.ColorShaderProgram


/**
 * 冰球
 */
class Puck(val radius: Float, val height: Float, numPointsAroundPuck: Int) {

    private val vertexArray: VertexArrayEs2
    private val drawList: List<DrawCommand>

    init {
        val generatedData: GeneratedData = ObjectBuilder.createPuck(
            Cylinder(Point(0f, 0f, 0f), radius, height),
            numPointsAroundPuck
        )

        vertexArray = VertexArrayEs2(generatedData.vertexData)
        drawList = generatedData.drawList
    }

    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 0
        )
    }

    fun draw() {
        for (drawCommand in drawList) {
            drawCommand.draw()
        }
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }


}