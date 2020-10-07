package com.focus617.myopengldemo.data

import android.opengl.GLES31.*
import timber.log.Timber
import java.nio.*
import kotlin.properties.Delegates

/**
 * 适用于创建后不再变化的对象
 */
class VertexBuffer(vertexData: FloatArray, indexData: ShortArray? = null) {

    var mVaoId by Delegates.notNull<Int>()
    var mVertexId by Delegates.notNull<Int>()
    var mElementId by Delegates.notNull<Int>()
    var withElement: Boolean = false

    init {
        // 创建缓存，并绑定缓存类型
        var mVAOBuf: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
        var mVBOBuf: IntBuffer = IntBuffer.allocate(2)  // 顶点缓存对象

        // allocate only on the first draw
        // Generate VBO ID
        glGenBuffers(mVBOBuf.capacity(), mVBOBuf)
        for (index in 0 until mVBOBuf.capacity()) {
            if (mVBOBuf.get(index) == 0)
                throw RuntimeException("Could not create a new vertex buffer object.")
        }
        // Generate VAO ID
        glGenVertexArrays(mVAOBuf.capacity(), mVAOBuf)
        for (index in 0 until mVAOBuf.capacity()) {
            if (mVAOBuf.get(index) == 0)
                throw RuntimeException("Could not create a new vertex array object.")
        }
        Timber.d("init(): Create VBO, ID: $mVBOBuf")

        // mVBOIds[O] - used to store vertex attribute data
        mVertexId = mVBOBuf.get(0)
        // mVBOIds[l] - used to store element indices
        mElementId = mVBOBuf.get(1)
        // mVAOIds[O] - used to store vertex array data
        mVaoId = mVAOBuf.get(0)

        setupVertices(vertexData)
        if (indexData != null) {
            setupElements(indexData)
            withElement = true
        }
    }

    private fun setupVertices(vertices: FloatArray) {

        Timber.d("setupVertices()")

        glBindBuffer(GL_ARRAY_BUFFER, mVertexId)

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
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementId)

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

    class AttributeProperty(
        val componentIndex: Int,
        val componentCount: Int,
        val stride: Int,
        val dataOffset: Int
    )

    fun bindData(
        attribProperties: List<AttributeProperty>
    ) {
        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(mVaoId)
        // Bind VBO buffer
        glBindBuffer(GL_ARRAY_BUFFER, mVertexId)

        for (attrib in attribProperties) {
            // 设置顶点属性
            glVertexAttribPointer(
                attrib.componentIndex,
                attrib.componentCount,
                GL_FLOAT,
                false,
                attrib.stride,
                attrib.dataOffset
            )
            // 启用顶点属性
            glEnableVertexAttribArray(attrib.componentIndex)
        }

        if (withElement) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementId)
        }
        // Reset to the default VAO
        glBindVertexArray(0)
    }

    fun drawWithElements(numElements: Int) {
        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(mVaoId)

        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)

        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}
