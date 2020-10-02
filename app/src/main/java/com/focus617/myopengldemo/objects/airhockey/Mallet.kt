package com.focus617.myopengldemo.objects.airhockey

import android.opengl.GLES31.*
import com.focus617.myopengldemo.data.VertexArray
import com.focus617.myopengldemo.programs.ColorShaderProgram

class Mallet {
    private val vertexArray: VertexArray = VertexArray(VERTEX_DATA)

    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        glDrawArrays(GL_POINTS, 0, 2)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private val STRIDE: Int = (
                (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Float.SIZE_BYTES)

        private val VERTEX_DATA = floatArrayOf(
            // Order of coordinates: X, Y, R, G, B
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
        )
    }

}
