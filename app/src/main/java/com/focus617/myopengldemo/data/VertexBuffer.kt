package com.focus617.myopengldemo.data

import android.opengl.GLES31.*
import timber.log.Timber
import java.nio.*

/**
 * 适用于创建后不再变化的对象
 */
class VertexBuffer(vertexData: FloatArray, indexData: ShortArray? = null) {

    var mVAOId: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
    var mVBOIds: IntBuffer = IntBuffer.allocate(2)  // 顶点缓存对象
    var withElement: Boolean = false

    init {
        // 创建缓存，并绑定缓存类型
        // mVBOIds[O] - used to store vertex attribute data
        // mVBOIds[l] - used to store element indices
        // allocate only on the first draw
        glGenBuffers(mVBOIds.capacity(), mVBOIds)
        for (index in 0 until mVBOIds.capacity()) {
            if (mVBOIds.get(index) == 0)
                throw RuntimeException("Could not create a new vertex buffer object.")
        }
        Timber.d("init(): Create VBO, ID: $mVBOIds")

        setupVertices(vertexData)
        if (indexData != null) {
            setupElements(indexData)
            withElement = true
        }
    }

    fun getVaoId(): Int = mVAOId.get(0);

    private fun setupVertices(vertices: FloatArray) {

        Timber.d("setupVertices()")

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))

        // Transfer data to native memory.
        val vertexArray: FloatBuffer = ByteBuffer
            .allocateDirect(vertices.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexArray.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(
            GL_ARRAY_BUFFER,
            vertexArray.capacity() * Float.SIZE_BYTES,
            vertexArray,
            GL_STATIC_DRAW
        )
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun setupElements(indices: ShortArray) {

        Timber.d("setupElements()")

        // bind buffer object for element indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(1))

        // Transfer data to native memory.
        val indexArray: ShortBuffer = ByteBuffer
            .allocateDirect(indices.size * Short.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices)
        indexArray.position(0)

        // Transfer data from native memory to the GPU buffer.

        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER,
            indexArray.capacity() * Short.SIZE_BYTES,
            indexArray,
            GL_STATIC_DRAW
        )
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }


    fun setVertexAttribPointer(
        dataOffset: Int, attributeLocation: Int, componentCount: Int, stride: Int
    ) {
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))
        // This call is slightly different than the glVertexAttribPointer we've
        // used in the past: the last parameter is set to dataOffset, to tell OpenGL
        // to begin reading data at this position of the currently bound buffer.
        glVertexAttribPointer(
            attributeLocation, componentCount, GL_FLOAT,
            false, stride, dataOffset
        )
        glEnableVertexAttribArray(attributeLocation)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
}
