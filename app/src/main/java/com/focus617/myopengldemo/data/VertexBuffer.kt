package com.focus617.myopengldemo.data

import android.opengl.GLES31
import android.opengl.GLES31.*
import timber.log.Timber
import java.nio.*
import kotlin.properties.Delegates

/**
 * 本对象负责将顶点属性和索引加载到GPU，并执行显示操作
 */
class VertexBuffer private constructor() {
    // OpenGL对象的句柄
    var mVaoId by Delegates.notNull<Int>()
    var mVertexId by Delegates.notNull<Int>()
    var mElementId by Delegates.notNull<Int>()

    var withElement: Boolean = false    // 本对象是否包含索引对象
    private var numVertices: Int = 0    // 顶点的数目
    private var numElements: Int = 0    // 索引的数目

    // 包含内存中的Buffer对象，用以动态更新顶点和索引数据（updateBuffer()）
    // 调用setupVertices和setupElements重新写入GPU
    private lateinit var mVertexArray: VertexArray
    private lateinit var mElementArray: ElementArray

    init {
        // 创建缓存，并绑定缓存类型
        var mVAOBuf: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
        var mVBOBuf: IntBuffer = IntBuffer.allocate(2)   // 顶点缓存对象

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
    }

    fun setupVertices() {

        Timber.d("setupVertices()")

        glBindBuffer(GL_ARRAY_BUFFER, mVertexId)

        // Reset to origin offset
        mVertexArray.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(
            GL_ARRAY_BUFFER,
            mVertexArray.capacity() * Float.SIZE_BYTES,
            mVertexArray.getFloatBuffer(),
            GL_STATIC_DRAW
        )
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun updateVertices(vertexArray: VertexArray, numVertex: Int) {

        Timber.d("updateVertices()")

        mVertexArray = vertexArray
        setupVertices()
        numVertices = numVertex
    }

    fun setupElements() {
        Timber.d("setupElements()")

        // bind buffer object for element indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementId)

        // Reset to origin offset
        mElementArray.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER,
            mElementArray.capacity() * Short.SIZE_BYTES,
            mElementArray.getShortBuffer(),
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

    fun bindData(attribProperties: List<AttributeProperty>) {
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

    fun draw() {
        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(mVaoId)

        if (withElement) {
            glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)
        } else{
            glDrawArrays(GL_POINTS, 0, numVertices)
        }

        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    companion object {

        fun build(vertices: FloatArray, indices: ShortArray? = null): VertexBuffer {
            val vertexBuffer = VertexBuffer()

            // Transfer data to native memory.
            vertexBuffer.mVertexArray = VertexArray(vertices)
            vertexBuffer.setupVertices()
            vertexBuffer.numVertices = vertices.size

            if (indices != null) {
                // Transfer data to native memory.
                vertexBuffer.mElementArray = ElementArray(indices)
                vertexBuffer.setupElements()
                vertexBuffer.withElement = true
                vertexBuffer.numElements = indices.size
            }
            return vertexBuffer
        }

        fun build(vertexArray: VertexArray, numVertex: Int): VertexBuffer {
            Timber.d("buildVertices")

            val vertexBuffer = VertexBuffer()
            vertexBuffer.mVertexArray = vertexArray
            vertexBuffer.setupVertices()
            vertexBuffer.numVertices = numVertex

            return vertexBuffer
        }
    }
}
