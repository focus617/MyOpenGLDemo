package com.focus617.myopengldemo.objects.geometry.d3

import android.content.Context
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.base.objectbuilder.IndexMeshObject
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder2
import timber.log.Timber

class Ball(
    context: Context,
    val radius: Float
): IndexMeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    override fun initVertexArray() {
        Timber.d("initVertexArray(radius=$radius)")
        initVertices(radius)      //初始化顶点坐标
//        initColors()              //初始化顶点颜色
    }

    //初始化顶点坐标
    private fun initVertices(radius: Float) {
        Timber.d("initVertices(radius=$radius)")
        val builder = ObjectBuilder2()
        builder.appendBall(radius)

        //顶点坐标数据的初始化
        build(builder.buildData())
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
            )
        )
        super.bindData(attribPropertyList)
    }

    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

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

        private const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_SIZE

        // 连续的顶点属性组之间的间隔
        private const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }



}