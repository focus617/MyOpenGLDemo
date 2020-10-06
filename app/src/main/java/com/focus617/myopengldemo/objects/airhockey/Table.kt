package com.focus617.myopengldemo.objects.airhockey

import android.opengl.GLES31
import android.opengl.GLES31.*
import com.focus617.myopengldemo.data.VertexArray
import com.focus617.myopengldemo.data.VertexBuffer
import com.focus617.myopengldemo.programs.airhockey.TextureShaderProgram
import com.focus617.myopengldemo.render.DrawingObject

class Table : DrawingObject() {

    // TODO: clean below ES2 implementation
    private val vertexArray = VertexArray(vertices)

    fun bindDataEs2(textureProgram: TextureShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            textureProgram.getPositionAttributeLocation(),
            VERTEX_POS_COMPONENT_COUNT,
            VERTEX_STRIDE
        )
        vertexArray.setVertexAttribPointer(
            VERTEX_POS_COMPONENT_COUNT,
            textureProgram.getTextureCoordinatesAttributeLocation(),
            VERTEX_TEXCOORDO_COMPONENT_COUNT,
            VERTEX_STRIDE
        )
    }

    fun drawEs2() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }


    ////////////////////////////////////////////////////////////
    private val vertexData = VertexBuffer(vertices, indices)

    fun bindDataEs3(textureProgram: TextureShaderProgram) {

        //Generate VAO ID
        glGenVertexArrays(vertexData.mVAOId.capacity(), vertexData.mVAOId)

        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(vertexData.mVAOId.get(0))

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        glBindBuffer(GL_ARRAY_BUFFER, vertexData.mVBOIds.get(0))

        // 顶点目前有两个属性：第一个是位置属性
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )
        // 顶点的第二个属性是纹理坐标
        glVertexAttribPointer(
            VERTEX_TEXCOORDO_INDEX,
            VERTEX_TEXCOORDO_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            (VERTEX_TEX_COORDO_OFFSET * Float.SIZE_BYTES)
        )

        // 启用顶点数组
        glEnableVertexAttribArray(VERTEX_POS_INDEX)
        glEnableVertexAttribArray(VERTEX_TEXCOORDO_INDEX)

        if (vertexData.withElement) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexData.mVBOIds.get(1))
        }

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    fun drawEs3() {
        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(vertexData.mVAOId.get(0))

        // 图元装配，绘制三角形
        glDrawElements(GL_TRIANGLES, 15, GL_UNSIGNED_SHORT, 0)

        glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    ////////////////////////////////////////////////////////////
    // 顶点数据集，及其属性
    companion object {

        // 假定每个顶点有4个顶点属性一位置、一个纹理坐标(以后会增加法线和两个纹理坐标)

        // 球桌的顶点属性
        // Order of coordinates: X, Y, S, T
        private val vertices = floatArrayOf(
            0f, 0f, 0.5f, 0.5f,         // 0 middle
            -0.5f, -0.8f, 0.0f, 0.9f,   // 1 bottom left
            0.5f, -0.8f, 1.0f, 0.9f,    // 2 bottom right
            0.5f, 0.8f, 1.0f, 0.1f,     // 3 top right
            -0.5f, 0.8f, 0.0f, 0.1f,    // 4 top left
            -0.5f, -0.8f, 0.0f, 0.9f    // 5 bottom left
        )

        // 球桌的顶点索引
        var indices = shortArrayOf(
            0, 1, 2,
            0, 2, 3,
            0, 3, 4,
            0, 4, 5
        )

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_TEXCOORDO_INDEX = 1
//        internal const val VERTEX_COLOR_INDEX = 1
//        internal const val VERTEX_NORMAL_INDEX = 1
//        internal const val VERTEX_TEXCOORDO_INDEX = 2
//        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_COMPONENT_COUNT = 2          // x,y
        internal const val VERTEX_TEXCOORDO_COMPONENT_COUNT = 2    // s,t
//        internal const val VERTEX_COLOR_COMPONENT_COUNT = 3      // r,g,b
//        internal const val VERTEX_NORMAL_COMPONENT_COUNT = 3     // x,y,z
//        internal const val VERTEX_TEXCOORD1_COMPONENT_COUNT = 2  // s,t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_TEX_COORDO_OFFSET = VERTEX_POS_COMPONENT_COUNT
//        internal const val VERTEX_COLOR_OFFSET = VERTEX_POS_SIZE
//        internal const val VERTEX_NORMAL_OFFSET = 3
//        internal const val VERTEX_TEX_COORD1_OFFSET = 8

        internal const val VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_COMPONENT_COUNT + VERTEX_TEXCOORDO_COMPONENT_COUNT

        // 顶点的数量
        internal val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }
}
