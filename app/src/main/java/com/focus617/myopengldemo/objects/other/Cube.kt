package com.focus617.myopengldemo.objects.other

import android.opengl.GLES31.*
import com.focus617.myopengldemo.data.VertexBuffer
import com.focus617.myopengldemo.render.DrawingObject

class Cube : DrawingObject() {

    private val vertexBuffer = VertexBuffer(vertices, indices)

    fun bindDataES3() {
        //Generate VAO ID
        glGenVertexArrays(vertexBuffer.mVAOId.capacity(), vertexBuffer.mVAOId)

        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(vertexBuffer.getVaoId())

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.mVBOIds.get(0))

        // 启用顶点数组
        glEnableVertexAttribArray(VERTEX_POS_INDEX)
        glEnableVertexAttribArray(VERTEX_COLOR_INDEX)

        // 顶点的位置属性
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )

        // 顶点的颜色属性
        glVertexAttribPointer(
            VERTEX_COLOR_INDEX,
            VERTEX_COLOR_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_COLOR_OFFSET
        )

        if (vertexBuffer.withElement) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBuffer.mVBOIds.get(1))
        }

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    fun drawES3() {
        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(vertexBuffer.getVaoId())

        // 图元装配，绘制三角形
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_SHORT, 0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)


        // Reset to the default VAO
        glBindVertexArray(0)
    }

    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 立方体的顶点属性
        // Order of coordinates: X, Y, Z, R, G, B
        private val vertices = floatArrayOf(
            -0.5f, 0.5f, 0.5f,     // (0) Top-left near
            0.3f, 0.4f, 0.5f,

            0.5f, 0.5f, 0.5f,      // (1) Top-right near
            0.3f, 0.4f, 0.5f,

            -0.5f, -0.5f, 0.5f,    // (2) Bottom-left near
            0.3f, 0.4f, 0.5f,

            0.5f, -0.5f, 0.5f,     // (3) Bottom-right near
            0.3f, 0.4f, 0.5f,

            -0.5f, 0.5f, -0.5f,    // (4) Top-left far
            0.6f, 0.5f, 0.4f,

            0.5f, 0.5f, -0.5f,     // (5) Top-right far
            0.6f, 0.5f, 0.4f,

            -0.5f, -0.5f, -0.5f,   // (6) Bottom-left far
            0.6f, 0.5f, 0.4f,

            0.5f, -0.5f, -0.5f,     // (7) Bottom-right far
            0.6f, 0.5f, 0.4f
        )

        // 顶点索引
        private val indices = shortArrayOf(
            // 6 indices per cube side
            // Front
            1, 3, 0,
            0, 3, 2,

            // Back
            4, 6, 5,
            5, 6, 7,

            // Left
            0, 2, 4,
            4, 2, 6,

            // Right
            5, 7, 1,
            1, 7, 3,

            // Top
            5, 1, 4,
            4, 1, 0,

            // Bottom
            6, 2, 7,
            7, 2, 3
        )

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_COLOR_INDEX = 1
//        internal const val VERTEX_TEXCOORDO_INDEX = 2
//        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_SIZE = 3          //x,y,and z
        internal const val VERTEX_COLOR_SIZE = 3       //x,y,and z
//        internal const val VERTEX_TEXCOORDO_SIZE = 2    //s and t
//        internal const val VERTEX_TEXCOORD1_SIZE = 2    //s and t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_COLOR_OFFSET = VERTEX_POS_SIZE
//        internal const val VERTEX_TEX_COORDO_OFFSET = 6
//        internal const val VERTEX_TEX_COORD1_OFFSET = 8

        internal const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_SIZE + VERTEX_COLOR_SIZE

        // 顶点的数量
        internal val vertexCount = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }
}