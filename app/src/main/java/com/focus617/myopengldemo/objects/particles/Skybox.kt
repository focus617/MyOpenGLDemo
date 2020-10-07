package com.focus617.myopengldemo.objects.particles

import android.opengl.GLES31.*
import com.focus617.myopengldemo.data.VertexBuffer
import com.focus617.myopengldemo.programs.particles.SkyboxShaderProgram

class Skybox {

    private val vertexBuffer = VertexBuffer(vertices, indices)

    fun bindDataES3(skyboxProgram: SkyboxShaderProgram) {
        //Generate VAO ID
        glGenVertexArrays(vertexBuffer.mVAOId.capacity(), vertexBuffer.mVAOId)

        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(vertexBuffer.getVaoId())

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.mVBOIds.get(0))

        // 顶点目前有一个位置属性
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
    
    companion object {

        // 立方体的顶点属性
        // Order of coordinates: X, Y, Z
        private val vertices = floatArrayOf(
            -1f, 1f, 1f,     // (0) Top-left near
            1f, 1f, 1f,      // (1) Top-right near
            -1f, -1f, 1f,    // (2) Bottom-left near
            1f, -1f, 1f,     // (3) Bottom-right near

            -1f, 1f, -1f,    // (4) Top-left far
            1f, 1f, -1f,     // (5) Top-right far
            -1f, -1f, -1f,   // (6) Bottom-left far
            1f, -1f, -1f     // (7) Bottom-right far
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
//        internal const val VERTEX_TEXCOORDO_INDEX = 1
//        internal const val VERTEX_COLOR_INDEX = 1
//        internal const val VERTEX_NORMAL_INDEX = 1
//        internal const val VERTEX_TEXCOORDO_INDEX = 2
//        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_COMPONENT_COUNT = 3          // x,y,z
//        internal const val VERTEX_TEXCOORDO_COMPONENT_COUNT = 2    // s,t
//        internal const val VERTEX_COLOR_COMPONENT_COUNT = 3      // r,g,b
//        internal const val VERTEX_NORMAL_COMPONENT_COUNT = 3     // x,y,z
//        internal const val VERTEX_TEXCOORD1_COMPONENT_COUNT = 2  // s,t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        internal const val VERTEX_POS_OFFSET = 0
//        internal const val VERTEX_TEX_COORDO_OFFSET = VERTEX_POS_COMPONENT_COUNT
//        internal const val VERTEX_COLOR_OFFSET = VERTEX_POS_SIZE
//        internal const val VERTEX_NORMAL_OFFSET = 3
//        internal const val VERTEX_TEX_COORD1_OFFSET = 8

        internal const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_COMPONENT_COUNT

        // 顶点的数量
        internal val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES
    }


}