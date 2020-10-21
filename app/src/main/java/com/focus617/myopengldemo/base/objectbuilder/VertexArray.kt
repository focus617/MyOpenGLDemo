package com.focus617.myopengldemo.base.objectbuilder

import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * 适用于创建后经常变化的顶点对象
 */
class VertexArray(vertices: FloatArray) {

    //提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer转换，
    //关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
    private val floatBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertices.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())         //设置字节顺序为本地操作系统顺序
        .asFloatBuffer()                        //转换为浮点(Float)型缓冲
        .put(vertices)                          //在缓冲区内写入数据

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

    fun dump(count: Int = 8) {
        Timber.d("VertexArray dump:")
        val buffSize = floatBuffer.capacity()
        Timber.d("VertexArray float buffer Size: $buffSize")

        if (buffSize <= count) {
            for (i in 0..buffSize)
                Timber.d("Vertex[$i]: ${floatBuffer[i]}")
        } else {
            for (i in 0..count)
                Timber.d("Vertex[$i]: ${floatBuffer[i]}")
            Timber.d("...")
            for (i in (buffSize - count) until buffSize)
                Timber.d("Vertex[$i]: ${floatBuffer[i]}")
        }
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

    fun dump() {
        Timber.d("ElementArray dump:")
        val buffSize = shortBuffer.capacity()
        Timber.d("ElementArray short buffer Size: $buffSize")

        if (buffSize < 6) {
            for (i in 0..buffSize)
                Timber.d("Element[$i]: ${shortBuffer[i]}")
        } else {
            for (i in 0..2)
                Timber.d("Element[$i]: ${shortBuffer[i]}")
            Timber.d("...")
            for (i in (buffSize - 3) until buffSize)
                Timber.d("Element[$i]: ${shortBuffer[i]}")
        }
    }
}
