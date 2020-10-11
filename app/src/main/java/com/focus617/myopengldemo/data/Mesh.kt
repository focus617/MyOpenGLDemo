package com.focus617.myopengldemo.data

import android.content.Context
import android.opengl.GLES31.*
import timber.log.Timber
import java.nio.*
import java.util.*
import kotlin.properties.Delegates

class Vertex {
    var position = FloatArray(3)
    var normal = FloatArray(3)
    var textureCoords = FloatArray(3)
}

class Texture {
    var id: Int = 0
    var type: String = ""   // 比如是漫反射贴图或者是镜面光贴图
}

/**
 * 本对象负责将顶点属性和索引加载到 GPU，并执行显示操作
 */
class Mesh private constructor() {
    // OpenGL对象的句柄
    var mVaoId by Delegates.notNull<Int>()
    private var mVertexId by Delegates.notNull<Int>()
    private var mElementId by Delegates.notNull<Int>()

    private var numVertices: Int = 0    // 顶点的数目
    private var numElements: Int = 0    // 索引的数目

    /*  网格数据  */
    var vertices = mutableListOf<Vertex>()
    var indices = mutableListOf<Short>()
    var textures = mutableListOf<Texture>()

    init {
        // 创建缓存，并绑定缓存类型
        val mVAOBuf: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
        val mVBOBuf: IntBuffer = IntBuffer.allocate(2)   // 顶点缓存对象

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

        val verticesBuffer: FloatBuffer = ByteBuffer
            .allocateDirect(vertices.size * VERTEX_COMPONENT_COUNT * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        for (vertex in vertices) {
            verticesBuffer.put(vertex.position)
//            verticesBuffer.put(vertex.normal)
//            verticesBuffer.put(vertex.textureCoords)
        }
        numVertices = vertices.size

        glBindBuffer(GL_ARRAY_BUFFER, mVertexId)

        // Reset to origin offset
        verticesBuffer.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(
            GL_ARRAY_BUFFER,
            verticesBuffer.capacity() * Float.SIZE_BYTES,
            verticesBuffer,
            GL_STATIC_DRAW
        )
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun setupElements() {
        Timber.d("setupElements()")

        val indicesBuffer: ShortBuffer = ByteBuffer
            .allocateDirect(indices.size * Short.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()

        for (index in indices) {
            indicesBuffer.put(index)
        }
        numElements = indices.size

        // bind buffer object for element indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementId)

        // Reset to origin offset
        indicesBuffer.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER,
            indicesBuffer.capacity() * Short.SIZE_BYTES,
            indicesBuffer,
            GL_STATIC_DRAW
        )
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun bindData() {
        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(mVaoId)
        // Bind VBO buffer
        glBindBuffer(GL_ARRAY_BUFFER, mVertexId)

        for (attrib in attribPropertyList) {
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

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementId)

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    fun draw() {
        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(mVaoId)

        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)

        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    companion object {

        class AttributeProperty(
            val componentIndex: Int,
            val componentCount: Int,
            val stride: Int,
            val dataOffset: Int
        )

        // 顶点坐标的每个属性的Index
        private const val VERTEX_POS_INDEX = 0
        private const val VERTEX_NORMAL_INDEX = 1
        private const val VERTEX_TEXCOORDO_INDEX = 2

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3            //x,y,z
        private const val VERTEX_NORMAL_SIZE = 3         //Nx, Ny, Nz
        private const val VERTEX_TEXCOORDO_SIZE = 3      //s, t and w

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        private const val VERTEX_POS_OFFSET = 0
        private const val VERTEX_NORMAL_OFFSET = VERTEX_POS_SIZE * Float.SIZE_BYTES
        private const val VERTEX_TEX_COORDO_OFFSET =
            (VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE) * Float.SIZE_BYTES

        private const val VERTEX_COMPONENT_COUNT =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORDO_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_COMPONENT_COUNT * Float.SIZE_BYTES

        private val attribPropertyList: List<AttributeProperty> = arrayListOf(
            // 顶点的位置属性
            AttributeProperty(
                VERTEX_POS_INDEX,
                VERTEX_POS_SIZE,
                VERTEX_STRIDE,
                VERTEX_POS_OFFSET
            ),

            // 顶点的法线
            AttributeProperty(
                VERTEX_NORMAL_INDEX,
                VERTEX_NORMAL_SIZE,
                VERTEX_STRIDE,
                VERTEX_NORMAL_OFFSET
            ),

            // 顶点的纹理坐标
            AttributeProperty(
                VERTEX_TEXCOORDO_INDEX,
                VERTEX_TEXCOORDO_SIZE,
                VERTEX_STRIDE,
                VERTEX_TEX_COORDO_OFFSET
            )
        )
    }
}


