package com.focus617.myopengldemo.objects.airhockey

import android.opengl.GLES31.*
import com.focus617.myopengldemo.data.VertexArrayEs2
import com.focus617.myopengldemo.data.VertexArrayEs3
import com.focus617.myopengldemo.objects.airhockey.ObjectBuilder.Companion.DrawCommand
import com.focus617.myopengldemo.objects.airhockey.ObjectBuilder.Companion.GeneratedData
import com.focus617.myopengldemo.programs.ColorShaderProgram
import com.focus617.myopengldemo.util.Geometry.Point

/**
 * 木槌
 */
class Mallet(val radius: Float, val height: Float, numPointsAroundMallet: Int) {

    private val generatedData: GeneratedData = ObjectBuilder.createMallet(
        Point(0f, 0f, 0f),
        radius,
        height,
        numPointsAroundMallet
    )
    private val vertexData = VertexArrayEs3(generatedData.vertexData)
    private val drawList: List<DrawCommand> = generatedData.drawList

    ////////////////////////////////////////////////////////////
    // TODO: clean below ES2 implementation
    private val vertexArray: VertexArrayEs2 = VertexArrayEs2(generatedData.vertexData)

    fun bindDataEs2(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            VERTEX_POS_COMPONENT_COUNT,
            VERTEX_STRIDE
        )
    }

    fun drawEs2() {
        for (drawCommand in drawList) {
            drawCommand.draw()
        }
    }

    ////////////////////////////////////////////////////////////

    fun bindDataEs3(colorProgram: ColorShaderProgram) {

        glBindBuffer(GL_ARRAY_BUFFER, vertexData.mVBOIds.get(0))

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        // 顶点目前有一个属性：位置坐标 (x,y,z)
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )

        // 启用顶点数组
        glEnableVertexAttribArray(VERTEX_POS_INDEX)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun drawEs3() {

        glBindBuffer(GL_ARRAY_BUFFER, vertexData.mVBOIds.get(0))

        // 图元装配，绘制木槌
        for (drawCommand in drawList) {
            drawCommand.draw()
        }

        // 禁用顶点数组
        glBindBuffer(GL_ARRAY_BUFFER, 0)
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
//        internal const val VERTEX_COLOR_OFFSET = VERTEX_POS_COMPONENT_COUNT
//        internal const val VERTEX_NORMAL_OFFSET = 3
//        internal const val VERTEX_TEX_COORDO_OFFSET = VERTEX_POS_COMPONENT_COUNT
//        internal const val VERTEX_TEX_COORD1_OFFSET = 8

        internal const val VERTEX_ATTRIBUTE_SIZE =  VERTEX_POS_COMPONENT_COUNT

        // 顶点的数量
//        internal val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES
    }

}
