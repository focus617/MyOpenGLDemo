package com.focus617.myopengldemo.objects.geometry.d3

import com.focus617.myopengldemo.base.objectbuilder.DrawingObject
import com.focus617.myopengldemo.base.objectbuilder.VertexBuffer
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty

/**
 * Compare with Cube, Cube2 has different normal for vertex, hasn't element
 */
class Cube2 : DrawingObject {

    private val vertexBuffer = VertexBuffer.build(vertices, VERTEX_COUNT)

    override fun bindData() {
        val attribPropertyList: List<AttributeProperty> = arrayListOf(
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
        vertexBuffer.bindData(attribPropertyList)
    }

    override fun draw() {
        vertexBuffer.draw()
    }

    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性:位置、法线和两个纹理坐标

        // 立方体的顶点属性
        private val vertices = floatArrayOf(
            // 背面
            // positions          // normals           // texture coords
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
            // 正面
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
            // 左侧
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            // 右侧
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
            // 底面
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
            // 顶面
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f

        )

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_NORMAL_INDEX = 1
        internal const val VERTEX_TEXCOORDO_INDEX = 2
//        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_SIZE = 3            //x,y,z
        internal const val VERTEX_NORMAL_SIZE = 3         //NX, NY, NZ
        internal const val VERTEX_TEXCOORDO_SIZE = 2      //s and t
//        internal const val VERTEX_TEXCOORD1_SIZE = 2    //s and t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_NORMAL_OFFSET = VERTEX_POS_SIZE * Float.SIZE_BYTES
        internal const val VERTEX_TEX_COORDO_OFFSET =
            (VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE) * Float.SIZE_BYTES
//        internal const val VERTEX_TEX_COORD1_OFFSET =

        internal const val VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORDO_SIZE

        // 顶点的数量
        internal val VERTEX_COUNT = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }
}