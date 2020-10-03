package com.focus617.myopengldemo.objects.airhockey

import android.opengl.GLES31
import com.focus617.myopengldemo.data.VertexArrayEs2
import com.focus617.myopengldemo.data.VertexArrayEs3
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.Geometry.Cylinder
import com.focus617.myopengldemo.objects.airhockey.ObjectBuilder.Companion.DrawCommand
import com.focus617.myopengldemo.objects.airhockey.ObjectBuilder.Companion.GeneratedData
import com.focus617.myopengldemo.programs.ColorShaderProgram


/**
 * 冰球
 */
class Puck(val radius: Float, val height: Float, numPointsAroundPuck: Int) {

    private val generatedData: GeneratedData = ObjectBuilder.createPuck(
            Cylinder(Point(0f, 0f, 0f), radius, height),
            numPointsAroundPuck
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
            VERTEX_POS_COMPONENT_COUNT, 0
        )
    }

    fun drawEs2() {
        for (drawCommand in drawList) {
            drawCommand.draw()
        }
    }

    ////////////////////////////////////////////////////////////

    fun bindDataEs3(colorProgram: ColorShaderProgram) {

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, vertexData.mVBOIds.get(0))

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        // 顶点目前有一个属性：位置坐标 (x,y,z)
        GLES31.glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_COMPONENT_COUNT,
            GLES31.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )

        // 启用顶点数组
        GLES31.glEnableVertexAttribArray(Mallet.VERTEX_POS_INDEX)

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
    }

    fun drawEs3() {

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, vertexData.mVBOIds.get(0))

        // 图元装配，绘制冰球
        for (drawCommand in drawList) {
            drawCommand.draw()
        }

        // 禁用顶点数组
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
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


}