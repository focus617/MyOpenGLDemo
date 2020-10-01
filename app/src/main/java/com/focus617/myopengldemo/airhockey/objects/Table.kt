package com.focus617.myopengldemo.airhockey.objects

import android.opengl.GLES31.*
import com.focus617.myopengldemo.data.VertexArray
import com.focus617.myopengldemo.programs.TextureShaderProgram

class Table {
    private val vertexArray= VertexArray(VERTEX_DATA)

    fun bindData(textureProgram: TextureShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            textureProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            textureProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2

        private val STRIDE: Int =
            (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Float.SIZE_BYTES

        private val VERTEX_DATA = floatArrayOf(
            // Triangle Fan
            // Order of coordinates: X, Y, S, T
               0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.8f,   0f, 0.9f,
             0.5f, -0.8f,   1f, 0.9f,
             0.5f,  0.8f,   1f, 0.1f,
            -0.5f,  0.8f,   0f, 0.1f,
            -0.5f, -0.8f,   0f, 0.9f
        )
    }
}
