package com.focus617.myopengldemo.objects.geometry.d3.ball

import android.content.Context
import com.focus617.myopengldemo.utils.Vector

/**
 * 表示地球的类，采用多重纹理
 */
class Earth(
    context: Context,
    radius: Float
) : Ball(context, radius) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
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
        viewPosition: Vector,
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

}