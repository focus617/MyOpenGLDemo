package com.focus617.myopengldemo.base.objectbuilder

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.Matrix
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import timber.log.Timber
import java.nio.*
import kotlin.properties.Delegates

/**
 * 本对象负责将顶点属性和索引加载到 GPU，并执行显示操作
 *  VertexBuffer 类的升级
 */
abstract class IndexMeshObject(val context: Context) : NewDrawingObject {

    //自定义渲染管线程序
    protected lateinit var mProgram: ShaderProgram

    // 创建缓存，并绑定缓存类型
    private val mVAOBuf: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
    private val mVBOBuf: IntBuffer = IntBuffer.allocate(4)   // 顶点缓存对象

    // OpenGL对象的句柄
    var mVaoId by Delegates.notNull<Int>()
    protected var mElementId by Delegates.notNull<Int>()
    protected var mVertexId by Delegates.notNull<Int>()
    protected var mNormalId by Delegates.notNull<Int>()
    protected var mTextureId by Delegates.notNull<Int>()

    protected var numVertices: Int = 0    // 顶点的数目
    protected var numElements: Int = 0    // 索引的数目

    // 网格数据: 包含内存中的Buffer对象，用以动态更新顶点和索引数据（updateBuffer()）
    // 调用setupVertices和setupElements重新写入GPU
    protected lateinit var mVertexArray: VertexArray
    protected lateinit var mElementArray: ElementArray

    // 保存本对象的模型矩阵
    val mModelMatrix = FloatArray(16)

    // 本对象的基准位置
    var mPosition: Vector = Vector(0.0f, 0.0f, 0.0f)

    init {
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

        // mVAOIds[O] - used to store vertex array data
        mVaoId = mVAOBuf.get(0)
        // mVBOIds[0] - used to store element indices
        mElementId = mVBOBuf.get(0)
        // mVBOIds[1] - used to store vertex data
        mVertexId = mVBOBuf.get(1)
        // mVBOIds[2] - used to store vertex normal attribute data
        mNormalId = mVBOBuf.get(2)
        // mVBOIds[3] - used to store vertex texture attribute data
        mTextureId = mVBOBuf.get(3)

        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    // 销毁纹理和缓冲区对象
    fun destroy() {
        glDeleteBuffers(5, mVBOBuf)
        glDeleteVertexArrays(1, mVAOBuf)
    }

    fun build(vertices: FloatArray, numVertex: Int, indices: ShortArray) {
        Timber.d("build(): vertices size=$numVertex")

        // Transfer data to native memory.
        mVertexArray = VertexArray(vertices)
        setupVertices()
        numVertices = numVertex

        Timber.d("build(): indices size=${indices.size}")
        // Transfer data to native memory.
        mElementArray = ElementArray(indices)
        setupElements()
        numElements = indices.size
    }


    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        // 初始化模型矩阵
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.translateM(mModelMatrix, 0, x, y, z)
    }

    fun positionObjectInScene() {
        positionObjectInScene(mPosition.x, mPosition.y, mPosition.z)
    }

    fun moveTo(position: Vector) {
        mPosition = position
        positionObjectInScene()
    }

    fun move(vector: Vector) {
        mPosition = mPosition.plus(vector)
        positionObjectInScene()
    }

    protected fun setupVertices() {

        Timber.d("setupVertices()")

        glBindBuffer(GL_ARRAY_BUFFER, mVertexId)

        // Reset 缓冲区起始位置 to origin offset
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

    protected fun updateVertices(vertexArray: VertexArray, numVertex: Int) {

        Timber.d("updateVertices()")

        mVertexArray = vertexArray
        setupVertices()
        numVertices = numVertex
    }

    protected fun setupElements() {
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

    fun bindData(attribPropertyList: List<AttributeProperty>) {
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

        // Reset to the default VAO
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun draw() {
        // 将程序添加到OpenGL ES环境
        mProgram.use()

        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(mVaoId)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementId)

        // 图元装配，绘制三角形
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)

        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

    }
}


