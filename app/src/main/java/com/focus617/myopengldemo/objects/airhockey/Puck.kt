package com.focus617.myopengldemo.objects.airhockey

import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.objectbuilder.DrawingObject
import com.focus617.myopengldemo.base.objectbuilder.VertexBuffer
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.Geometry.Cylinder
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder.Companion.DrawCommand
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder.Companion.GeneratedData


/**
 * 冰球
 */
class Puck(val radius: Float, val height: Float, numPointsAroundPuck: Int): DrawingObject {

    private val generatedData: GeneratedData = AirHockeyObjectBuilder.createPuck(
            Cylinder(Point(0f, 0f, 0f), radius, height),
            numPointsAroundPuck
        )
    private val vertexBuffer =
        VertexBuffer.build(generatedData.vertexArray, generatedData.vertexNumber)
    private val drawList: List<DrawCommand> = generatedData.drawList

    override fun bindData() {

        val attribPropertyList: List<VertexBuffer.AttributeProperty> = arrayListOf(
            VertexBuffer.AttributeProperty(
                VERTEX_POS_INDEX,
                VERTEX_POS_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_POS_OFFSET
            )
        )
        vertexBuffer.bindData(attribPropertyList)
    }

    override fun draw() {
        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(vertexBuffer.mVaoId)

        // 图元装配，绘制冰球
        for (drawCommand in drawList) {
            drawCommand.draw()
        }
        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

    }

    companion object {

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_COMPONENT_COUNT = 3          // x,y,z

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        // of structures
        internal const val VERTEX_POS_OFFSET = 0

        internal const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_COMPONENT_COUNT

        // 顶点的数量
//        internal val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES
    }

    ////////////////////////////////////////////////////////////
    // TODO: clean below ES2 implementation
//    private val vertexArray: VertexArray = VertexArray(generatedData.vertexArray)
//
//    fun bindDataEs2(colorProgram: ColorShaderProgram) {
//        vertexArray.setVertexAttribPointer(
//            0,
//            colorProgram.getPositionAttributeLocation(),
//            VERTEX_POS_COMPONENT_COUNT, 0
//        )
//    }
//
//    fun drawEs2() {
//        for (drawCommand in drawList) {
//            drawCommand.draw()
//        }
//    }

}