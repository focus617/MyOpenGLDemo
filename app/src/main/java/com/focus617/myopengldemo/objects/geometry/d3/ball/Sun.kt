package com.focus617.myopengldemo.objects.geometry.d3.ball

import android.content.Context

/**
 * 表示地球的类，采用多重纹理
 */
class Sun(
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
//        mProgram = BallShaderProgram(context)
        mProgram = SunShaderProgram(context)
        bindData()
    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ) {
        mProgram.use()
        (mProgram as SunShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix
        )
    }

}