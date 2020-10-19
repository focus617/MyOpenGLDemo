package com.focus617.myopengldemo.objects.geometry.triangle

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.VertexArray
import com.focus617.myopengldemo.base.basic.Camera
import com.focus617.myopengldemo.base.objectbuilder.MeshObject
import com.focus617.myopengldemo.programs.ShaderConstants
import com.focus617.myopengldemo.util.Geometry
import kotlin.properties.Delegates


class Triangle(context: Context) : MeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    //初始化顶点数据的方法
    override fun initVertexArray() {

        //顶点坐标数据的初始化
        numVertices = 3

        val UNIT_SIZE = 0.3f
        val vertices = floatArrayOf(
            -4 * UNIT_SIZE, 0f, 0f,
            0f, -4 * UNIT_SIZE, 0f,
            4 * UNIT_SIZE, 0f, 0f
        )

        // Transfer data to native memory.
        mVertexArray = VertexArray(vertices)

        // Reset 缓冲区起始位置 to origin offset
        mVertexArray.position(0)

        val colors = floatArrayOf(
         // R,  G,  B,  Alpha
            1f, 1f, 1f, 0f,
            0f, 0f, 1f, 0f,
            0f, 1f, 0f, 0f
        )

        // Transfer data to native memory.
        mColorArray = VertexArray(colors)

        // Reset 缓冲区起始位置 to origin offset
        mColorArray.position(0)
    }

    //初始化Shader Program
    override fun initShader() {
        //自定义渲染管线程序
        mProgram = TriangleShaderProgram(context)
        bindData()
    }

    override fun bindData() {
        mProgram.use()

        //获取程序中顶点位置属性引用
        val maPositionHandle = glGetAttribLocation(mProgram.getId(), ShaderConstants.A_POSITION)
        //顶点颜色属性引用
        val maColorHandle = glGetAttribLocation(mProgram.getId(), ShaderConstants.A_COLOR)
        
        //将顶点位置数据传送进渲染管线
        glVertexAttribPointer(
            maPositionHandle,
            3,
            GL_FLOAT,
            false,
            3 * 4,
            mVertexArray.getFloatBuffer()
        )
        //将顶点颜色数据传送进渲染管线
        glVertexAttribPointer(
            maColorHandle,
            4,
            GL_FLOAT,
            false,
            4 * 4,
            mColorArray.getFloatBuffer()
        )
        glEnableVertexAttribArray(maPositionHandle) //启用顶点位置数据

        glEnableVertexAttribArray(maColorHandle) //启用顶点着色数据
    }


    override fun draw() {
        mProgram.use()
        //绘制三角形
        glDrawArrays(GL_TRIANGLES, 0, numVertices)

    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {
        mProgram.use()
        (mProgram as TriangleShaderProgram).setUniforms(modelMatrix, viewMatrix, projectionMatrix)
    }

}