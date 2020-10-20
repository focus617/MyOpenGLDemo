package com.focus617.myopengldemo.objects.geometry.d3

import android.content.Context
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.base.objectbuilder.IndexMeshObject

class Cube(context: Context): IndexMeshObject(context) {

    override fun initVertexArray() {
        build(vertices, VERTEX_COUNT, indices)
    }

    override fun initShader() {
        //自定义渲染管线程序
        mProgram = LightCubeShaderProgram(context)
        bindData()
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
        super.bindData(attribPropertyList)
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