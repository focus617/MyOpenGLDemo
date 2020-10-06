package com.focus617.myopengldemo.data

import android.opengl.GLES31.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 适用于创建后经常变化的对象
 */
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

    /**
     * Updates the float buffer with the specified vertex data, assuming that
     * the vertex data and the float buffer are the same size.
     */
    fun updateBuffer(vertexData: FloatArray, start: Int, count: Int) {
        floatBuffer.position(start)
        floatBuffer.put(vertexData, start, count)
        floatBuffer.position(0)
    }
}

