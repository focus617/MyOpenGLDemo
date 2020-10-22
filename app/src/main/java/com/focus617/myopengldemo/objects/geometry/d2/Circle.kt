package com.focus617.myopengldemo.objects.geometry.d2

import android.content.Context
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.base.objectbuilder.IndexMeshObject
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder2
import com.focus617.myopengldemo.objects.geometry.d3.ball.EarthShaderProgram
import com.focus617.myopengldemo.util.Geometry
import timber.log.Timber

/**
 * 表示地球的类，采用多重纹理
 */
class Circle(
    context: Context,
    val radius: Float,
    val numPoints: Int = 40
) : IndexMeshObject(context) {

    val UNIT_SIZE: Float = 1f

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    override fun initVertexArray() {
        Timber.d("initVertexArray(radius=$radius)")
        initVertices(radius)      //初始化顶点坐标
    }

    //初始化顶点坐标
    private fun initVertices(radius: Float) {
        Timber.d("initVertices(radius=$radius)")
        val builder = ObjectBuilder2()
        builder.appendCircle(radius, numPoints, 0f)
        builder.appendCone(radius, 2f, numPoints)

        //顶点坐标数据的初始化
        build(builder.buildData())
//        mVertexArray.dump(16)
        mElementArray.dump(40)
    }

    override fun initShader() {
        //自定义渲染管线程序
        mProgram = EarthShaderProgram(context)
        bindData()
    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        viewPosition: Geometry.Companion.Vector,
        earthDayTextureId: Int,
        earthNightTextureId: Int
    ) {
        mProgram.use()
        (mProgram as EarthShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix,
            viewPosition,
            earthDayTextureId,
            earthNightTextureId,
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


    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 顶点坐标的每个属性的Index
        private const val VERTEX_POS_INDEX = 0
        private const val VERTEX_NORMAL_INDEX = 1
        internal const val VERTEX_TEXCOORDO_INDEX = 2

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3            //x,y,z
        private const val VERTEX_NORMAL_SIZE = 3         //NX, NY, NZ
        private const val VERTEX_TEXCOORDO_SIZE = 2      //s and t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        private const val VERTEX_POS_OFFSET = 0
        private const val VERTEX_NORMAL_OFFSET = VERTEX_POS_SIZE * Float.SIZE_BYTES
        private const val VERTEX_TEX_COORDO_OFFSET =
            (VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE) * Float.SIZE_BYTES

        private const val VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORDO_SIZE

        // 连续的顶点属性组之间的间隔
        private const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }


}