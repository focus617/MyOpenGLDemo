package com.focus617.myopengldemo.base

import android.content.Context
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.base.objectbuilder.IndexMeshObject
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder2
import com.focus617.myopengldemo.utils.Vector


/**
 * 本对象表示具备顶点坐标，法向和纹理的最小绘制单位
 */
class Mesh(
    context: Context,
    val materialKey: String,
    val data: ObjectBuilder2.Companion.GeneratedData
): IndexMeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    override fun initVertexArray() {
        //顶点坐标数据的初始化
        build(data)
        mVertexArray.dump()
        mElementArray.dump()
    }

    override fun initShader() {
        //自定义渲染管线程序
        mProgram = MeshShaderProgram(context)
        bindData()
    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        viewPosition: Vector,
        textureId: Int
    ) {
        mProgram.use()
        (mProgram as MeshShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix,
            viewPosition,
            textureId
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
        super.bindData(attribPropertyList)
    }

    companion object {

        // 顶点坐标的每个属性的Index
        private const val VERTEX_POS_INDEX = 0
        private const val VERTEX_NORMAL_INDEX = 1
        private const val VERTEX_TEXCOORDO_INDEX = 2

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3            //x,y,z
        private const val VERTEX_NORMAL_SIZE = 3         //NX, NY, NZ
        private const val VERTEX_TEXCOORDO_SIZE = 2      //s,t, w

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        private const val VERTEX_POS_OFFSET = 0
        private const val VERTEX_NORMAL_OFFSET = VERTEX_POS_SIZE * Float.SIZE_BYTES
        private const val VERTEX_TEX_COORDO_OFFSET =
            (VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE) * Float.SIZE_BYTES

        private const val VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + (VERTEX_TEXCOORDO_SIZE+1)

        // 连续的顶点属性组之间的间隔
        private const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }


}


