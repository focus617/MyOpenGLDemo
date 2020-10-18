package com.focus617.myopengldemo.base.objectbuilder

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.ElementArray
import com.focus617.myopengldemo.base.VertexArray
import com.focus617.myopengldemo.programs.ShaderConstants.A_COLOR
import com.focus617.myopengldemo.programs.ShaderConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.other.CommonShaderProgram
import timber.log.Timber
import java.nio.*
import kotlin.properties.Delegates

data class Vertex(
    var position: FloatArray = FloatArray(3),
    var normal: FloatArray = FloatArray(3),
    var textureCoords: FloatArray = FloatArray(3)
)

enum class TextureType {
    TextureDiffuse,        // 漫反射纹理
    TextureSpecular        // 镜面光纹理
}

data class Texture(
    var id: Int,
    var type: TextureType,
    var fileName: String
)

/**
 * 本对象负责将顶点属性和索引加载到 GPU，并执行显示操作
 */
abstract class MeshObject(val context: Context) {

    //自定义渲染管线程序
    protected var mProgram = CommonShaderProgram(context)
    //获取程序中顶点位置属性引用
    protected var maPositionHandle = glGetAttribLocation(mProgram.getId(), A_POSITION)
    //顶点颜色属性引用
    protected var maColorHandle = glGetAttribLocation(mProgram.getId(), A_COLOR)

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
    var textures = mutableListOf<Texture>()

    init {
        // 创建缓存，并绑定缓存类型
        val mVAOBuf: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
        val mVBOBuf: IntBuffer = IntBuffer.allocate(5)   // 顶点缓存对象

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
    }

    //初始化顶点数据的方法
    abstract fun initVertexArray()

    fun getProgram() = mProgram

    fun setupVertices() {

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

    fun setupColors() {

        Timber.d("setupColors()")

        glBindBuffer(GL_ARRAY_BUFFER, mColorId)

        // Reset 缓冲区起始位置 to origin offset
        mColorArray.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(
            GL_ARRAY_BUFFER,
            mColorArray.capacity() * Float.SIZE_BYTES,
            mColorArray.getFloatBuffer(),
            GL_STATIC_DRAW
        )

        glBindBuffer(GL_ARRAY_BUFFER, 0)
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

    private fun setTextures(shaderProgram: ShaderProgram) {

        val PreFix = "material."

        var diffuseNr = 1
        var specularNr = 1

        for ((index, texture) in textures.withIndex()) {
            // 在绑定之前激活相应的纹理单元
            glActiveTexture(GL_TEXTURE0 + index);

            // 获取纹理序号（diffuse_textureN 中的 N）
            var name: String
            var number: String

            when (textures[index].type) {
                TextureType.TextureDiffuse -> {
                    name = "texture_diffuse"
                    number = (diffuseNr++).toString()
                }
                TextureType.TextureSpecular -> {
                    name = "texture_specular"
                    number = (specularNr++).toString()
                }
            }

            /* 纹理命名标准：
             * 每个漫反射纹理被命名为texture_diffuseN，
             * 每个镜面光纹理应该被命名为texture_specularN，
             * 其中N的范围是1到纹理采样器最大允许的数字。
             */
            shaderProgram.setInt((PreFix + name + number), index);

            glBindTexture(GL_TEXTURE_2D, textures[index].id);
        }
        glActiveTexture(GL_TEXTURE0);
    }

    open fun draw() {

        mProgram.use()

        // TODO: pass parameter to GPU
        // shaderProgram.setUniforms()

        setTextures(mProgram)

        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(mVaoId)

        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)

        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    // 销毁纹理和缓冲区对象
//    fun destroy(){
//        for (texture in textures) {
//            glDeleteTextures(1, texture.id);
//        }
//        glDeleteBuffers(1, mElementId)
//        glDeleteBuffers(1, numVertices)
//        glDeleteVertexArrays(1,mVaoId)
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


