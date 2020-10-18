package com.focus617.myopengldemo.base

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * 适用于创建后经常变化的顶点对象
 */
class VertexArray(vertices: FloatArray) {

    private val floatBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertices.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertices)

    fun getFloatBuffer() = floatBuffer

    fun position(pos: Int) = floatBuffer.position(pos)
    
    fun capacity() = floatBuffer.capacity()
    

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

/**
 * 适用于创建后经常变化的顶点索引对象
 */
class ElementArray(indices: ShortArray) {

    private val shortBuffer: ShortBuffer = ByteBuffer
        .allocateDirect(indices.size * Short.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer()
        .put(indices)

    fun getShortBuffer() = shortBuffer

    fun position(pos: Int) = shortBuffer.position(pos)

    fun capacity() = shortBuffer.capacity()

    /**
     * Updates the float buffer with the specified vertex data, assuming that
     * the vertex data and the float buffer are the same size.
     */
    fun updateBuffer(indices: ShortArray, start: Int, count: Int) {
        shortBuffer.position(start)
        shortBuffer.put(indices, start, count)
        shortBuffer.position(0)
    }
}
