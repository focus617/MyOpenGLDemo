package com.focus617.myopengldemo.objects.geometry.d3.cube

import android.content.Context

class LightCube(context: Context): Cube(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
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

}