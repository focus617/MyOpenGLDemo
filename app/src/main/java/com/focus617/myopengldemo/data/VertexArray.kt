package com.focus617.myopengldemo.data

import android.opengl.GLES31.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(vertexData: FloatArray) {

    private val floatBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertexData.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertexData)

    fun setVertexAttribPointer(
        dataOffset: Int,
        attributeLocation: Int,
        componentCount: Int,
        stride: Int
    ) {
        floatBuffer.position(dataOffset)
        glVertexAttribPointer(
            attributeLocation,
            componentCount,
            GL_FLOAT,
            false,
            stride,
            floatBuffer
        )
        glEnableVertexAttribArray(attributeLocation)
        floatBuffer.position(0)
    }

}
