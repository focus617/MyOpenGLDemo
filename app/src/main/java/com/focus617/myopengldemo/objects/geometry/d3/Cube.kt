package com.focus617.myopengldemo.objects.geometry.d3

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.objectbuilder.VertexArray
import com.focus617.myopengldemo.base.objectbuilder.ElementArray
import com.focus617.myopengldemo.base.objectbuilder.MeshObject
import com.focus617.myopengldemo.base.objectbuilder.MeshObject.Companion.AttributeProperty
import timber.log.Timber

class Cube(context: Context): MeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    override fun initVertexArray() {

        numVertices = VERTEX_COUNT
        Timber.d("initVertexArray(): vertex number = $numVertices")

        //顶点数据的初始化
        mVertexArray = VertexArray(vertices)
        // 将顶点数据存入缓冲区
        setupVertices()

        numElements = indices.size
        Timber.d("initVertexArray(): element number = $numElements")

        mElementArray = ElementArray(indices)
        setupElements()
    }

    override fun initShader() {
        //自定义渲染管线程序
        mProgram = LightCubeShaderProgram(context)
        bindData()
    }

    override fun bindData() {
        val attribPropertyList: List<AttributeProperty> = arrayListOf(
            // 顶点的位置属性
            AttributeProperty(
                VERTEX_POS_INDEX,
                VERTEX_POS_SIZE,
                VERTEX_STRIDE,
                VERTEX_POS_OFFSET
            ),

            // 顶点的颜色属性
            AttributeProperty(
                VERTEX_COLOR_INDEX,
                VERTEX_COLOR_SIZE,
                VERTEX_STRIDE,
                VERTEX_COLOR_OFFSET
            )
        )

        mProgram.use()

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

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {
        mProgram.use()
        (mProgram as LightCubeShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix
        )
    }

    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 立方体的顶点属性
        // Order of coordinates: X, Y, Z, R, G, B
        private val vertices = floatArrayOf(
            -0.5f, 0.5f, 0.5f,     // (0) Top-left near
            0.3f, 0.4f, 0.5f,

            0.5f, 0.5f, 0.5f,      // (1) Top-right near
            0.3f, 0.4f, 0.5f,

            -0.5f, -0.5f, 0.5f,    // (2) Bottom-left near
            0.3f, 0.4f, 0.5f,

            0.5f, -0.5f, 0.5f,     // (3) Bottom-right near
            0.3f, 0.4f, 0.5f,

            -0.5f, 0.5f, -0.5f,    // (4) Top-left far
            0.6f, 0.5f, 0.4f,

            0.5f, 0.5f, -0.5f,     // (5) Top-right far
            0.6f, 0.5f, 0.4f,

            -0.5f, -0.5f, -0.5f,   // (6) Bottom-left far
            0.6f, 0.5f, 0.4f,

            0.5f, -0.5f, -0.5f,     // (7) Bottom-right far
            0.6f, 0.5f, 0.4f
        )

        // 顶点索引
        private val indices = shortArrayOf(
            // 6 indices per cube side
            // Front
            1, 0, 3,
            3, 0, 2,

            // Back
            4, 5, 6,
            5, 7, 6,

            // Left
            2, 0, 4,
            4, 6, 2,

            // Right
            5, 1, 7,
            7, 1, 3,

            // Top
            5, 4, 1,
            1, 4, 0,

            // Bottom
            6, 7, 2,
            2, 7, 3
        )

        // 顶点坐标的每个属性的Index
        private const val VERTEX_POS_INDEX = 0
        private const val VERTEX_COLOR_INDEX = 1

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3            //x,y,z
        private const val VERTEX_COLOR_SIZE = 3          //r,g,b

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        private const val VERTEX_POS_OFFSET = 0
        private const val VERTEX_COLOR_OFFSET = VERTEX_POS_SIZE * Float.SIZE_BYTES

        private const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_SIZE + VERTEX_COLOR_SIZE

        // 顶点的数量
        private val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        private const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }
}