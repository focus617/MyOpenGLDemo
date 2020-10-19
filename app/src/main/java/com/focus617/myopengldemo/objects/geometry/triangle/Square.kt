package com.focus617.myopengldemo.objects.geometry.triangle

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.objectbuilder.ElementArray
import com.focus617.myopengldemo.base.objectbuilder.MeshObject
import com.focus617.myopengldemo.base.objectbuilder.VertexArray
import com.focus617.myopengldemo.base.program.ShaderConstants
import timber.log.Timber
import kotlin.math.sin

class Square(context: Context) : MeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    //初始化顶点数据的方法
    override fun initVertexArray() {
        //顶点坐标数据的初始化
        mVertexArray = VertexArray(vertices)
        numVertices = VERTEX_COUNT
        Timber.d("initVertexArray(): vertex number = $numVertices")

        // 将顶点数据存入缓冲区
        setupVertices()

        // Transfer color data to native memory.
        mColorArray = VertexArray(colors)

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

        // Transfer element data to native memory.
        mElementArray = ElementArray(indices)
        numElements = indices.size
        setupElements()
    }

    override fun initShader() {
        //自定义渲染管线程序
        mProgram = SimpleShapeShaderProgram(context)
        bindData()
    }

    override fun bindData() {
        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(mVaoId)

        // Bind VBO buffer for Vertex Position
        glBindBuffer(GL_ARRAY_BUFFER, mVertexId)
        // 设置顶点属性
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )
        // 启用顶点属性
        glEnableVertexAttribArray(VERTEX_POS_INDEX)

        // Bind VBO buffer for Vertex Color
        glBindBuffer(GL_ARRAY_BUFFER, mColorId)
        // 设置顶点属性
        glVertexAttribPointer(
            VERTEX_COLOR_INDEX,
            VERTEX_COLOR_SIZE,
            GL_FLOAT,
            false,
            VERTEX_COLOR_SIZE * Float.SIZE_BYTES,
            VERTEX_COLOR_OFFSET
        )
        // 启用顶点属性
        glEnableVertexAttribArray(VERTEX_COLOR_INDEX)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementId)

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    override fun draw() {
        // 将程序添加到OpenGL ES环境
        mProgram.use()

        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(mVaoId)

        // 图元装配，绘制三角形
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)

        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {

        mProgram.use()

//        setupSolidColor()

        (mProgram as SimpleShapeShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix
        )
    }

    private fun setupSolidColor() {
        //顶点颜色属性引用
        mProgram.setVector4fv(
            ShaderConstants.A_COLOR,
            floatArrayOf(1.0f, 0.5f, 0.2f, 1.0f), 1
        )
    }

    private fun setupBlinkColor() {
        // 使用sin函数让颜色随时间在0.0到1.0之间改变
        val timeValue = System.currentTimeMillis()
        val greenValue = sin((timeValue / 300 % 50).toDouble()) / 2 + 0.5

        //顶点颜色属性引用
        mProgram.setVector4fv(
            ShaderConstants.A_COLOR,
            floatArrayOf(greenValue.toFloat(), 0.5f, 0.2f, 1.0f), 1
        )
    }


    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3          //x,y,and z
        private const val VERTEX_COLOR_SIZE = 4          //R,G,B,Alpha

        // 顶点坐标的每个属性的Index
        private const val VERTEX_POS_INDEX = 0
        private const val VERTEX_COLOR_INDEX = 1

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        private const val VERTEX_POS_OFFSET = 0
        private const val VERTEX_COLOR_OFFSET = 0

        private const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_SIZE

        // 正方形的顶点
        val UNIT_SIZE = 0.5f
        private var vertices = floatArrayOf(  // 按逆时针顺序
            -1 * UNIT_SIZE, 1 * UNIT_SIZE, 0.0f,   // top left
            -1 * UNIT_SIZE, -1 * UNIT_SIZE, 0.0f,  // bottom left
            1 * UNIT_SIZE, -1 * UNIT_SIZE, 0.0f,   // bottom right
            1 * UNIT_SIZE, 1 * UNIT_SIZE, 0.0f     // top right
        )

        private val colors = floatArrayOf(
            // R,  G,  B,  Alpha
            1f, 1f, 1f, 0f,
            0f, 0f, 1f, 0f,
            0f, 1f, 0f, 0f,
            1f, 0f, 0f, 0f
        )

        // 顶点的数量
        private val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        private const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

        // 顶点索引
        private var indices = shortArrayOf(
            0, 1, 2, 0, 2, 3
        )

    }

}