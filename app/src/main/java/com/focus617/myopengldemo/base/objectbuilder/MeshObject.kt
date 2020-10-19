package com.focus617.myopengldemo.base.objectbuilder

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.Matrix
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import timber.log.Timber
import java.nio.*
import java.text.FieldPosition
import kotlin.properties.Delegates

/**
 * 本对象负责将顶点属性和索引加载到 GPU，并执行显示操作
 */
abstract class MeshObject(val context: Context) {

    //自定义渲染管线程序
    protected lateinit var mProgram: ShaderProgram
    // 创建缓存，并绑定缓存类型
    private val mVAOBuf: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
    private val mVBOBuf: IntBuffer = IntBuffer.allocate(5)   // 顶点缓存对象

    // OpenGL对象的句柄
    var mVaoId by Delegates.notNull<Int>()
    protected var mElementId by Delegates.notNull<Int>()

    protected var mVertexId by Delegates.notNull<Int>()
    protected var mNormalId by Delegates.notNull<Int>()
    protected var mTextureId by Delegates.notNull<Int>()
    protected var mColorId by Delegates.notNull<Int>()

    protected var numVertices: Int = 0    // 顶点的数目
    protected var numElements: Int = 0    // 索引的数目

    // 网格数据: 包含内存中的Buffer对象，用以动态更新顶点和索引数据（updateBuffer()）
    // 调用setupVertices和setupElements重新写入GPU
    lateinit var mVertexArray: VertexArray
    lateinit var mColorArray: VertexArray
    lateinit var mElementArray: ElementArray

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
        // mVBOIds[4] - used to store vertex color attribute data
        mColorId = mVBOBuf.get(4)

        // 初始化模型矩阵
        Matrix.setIdentityM(mModelMatrix, 0)
    }

    // 销毁纹理和缓冲区对象
    fun destroy(){
        glDeleteBuffers(5, mVBOBuf)
        glDeleteVertexArrays(1, mVAOBuf)
    }

    //初始化Shader Program
    abstract fun initShader()

    //初始化顶点数据的方法
    abstract fun initVertexArray()

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.translateM(mModelMatrix, 0, x, y, z)
    }

    fun positionObjectInScene() {
        positionObjectInScene(mPosition.x, mPosition.y, mPosition.z)
    }

    fun moveTo(position: Vector){
        mPosition = position
        positionObjectInScene()
    }

    // 绘制方法
    abstract fun draw()


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

    open fun bindData() {
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

//    open fun draw() {
//
//        glUseProgram(mProgram)
//
//        // TODO: pass parameter to GPU
//        // shaderProgram.setUniforms()
//
//        setTextures(mProgram)
//
//        // Bind the VAO and then draw with VAO settings
//        glBindVertexArray(mVaoId)
//
//        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)
//
//        // Reset to the default VAO
//        glBindVertexArray(0)
//
//        glBindBuffer(GL_ARRAY_BUFFER, 0)
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
//    }



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


