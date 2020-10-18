package com.focus617.myopengldemo.objects.airhockey

import android.opengl.GLES31
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.DrawingObject
import com.focus617.myopengldemo.base.VertexBuffer
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder.Companion.DrawCommand
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder.Companion.GeneratedData
import com.focus617.myopengldemo.util.Geometry.Point

/**
 * 木槌
 */
class Mallet(val radius: Float, val height: Float, numPointsAroundMallet: Int): DrawingObject() {

    private val generatedData: GeneratedData = AirHockeyObjectBuilder.createMallet(
        Point(0f, 0f, 0f),
        radius,
        height,
        numPointsAroundMallet
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

        glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
        glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    companion object {

        // 假定每个顶点有4个顶点属性一位置、一个纹理坐标(以后会增加法线和两个纹理坐标)

        // 木槌的顶点属性
        // Order of coordinates: X, Y, R, G, B
//        private val vertices = floatArrayOf(
//            0f, -0.4f, 0f, 0f, 1f,
//            0f, 0.4f, 1f, 0f, 0f
//        )

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
//        internal const val VERTEX_COLOR_INDEX = 1
//        internal const val VERTEX_NORMAL_INDEX = 1
//        internal const val VERTEX_TEXCOORDO_INDEX = 2
//        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_COMPONENT_COUNT = 3          // x,y,z
//        internal const val VERTEX_COLOR_COMPONENT_COUNT = 3      // r,g,b
//        internal const val VERTEX_NORMAL_COMPONENT_COUNT = 3     // x,y,z
//        internal const val VERTEX_TEXCOORDO_COMPONENT_COUNT = 2  // s,t
//        internal const val VERTEX_TEXCOORD1_COMPONENT_COUNT = 2  // s,t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        // of structures
        internal const val VERTEX_POS_OFFSET = 0
//        internal const val VERTEX_COLOR_OFFSET = VERTEX_POS_COMPONENT_COUNT * Float.SIZE_BYTES
//        internal const val VERTEX_NORMAL_OFFSET =
//        internal const val VERTEX_TEX_COORDO_OFFSET =
//        internal const val VERTEX_TEX_COORD1_OFFSET =

        internal const val VERTEX_ATTRIBUTE_SIZE =  VERTEX_POS_COMPONENT_COUNT

        // 顶点的数量
//        internal val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES
    }

}
