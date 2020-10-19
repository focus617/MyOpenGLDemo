package com.focus617.myopengldemo.objects.geometry.triangle

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.objectbuilder.VertexArray
import com.focus617.myopengldemo.base.objectbuilder.MeshObject
import com.focus617.myopengldemo.base.program.ShaderConstants
import timber.log.Timber


class Triangle(context: Context) : MeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    //初始化顶点数据的方法
    override fun initVertexArray() {

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

        //顶点坐标数据的初始化
        numVertices = vertices.size/VERTEX_POS_SIZE
        Timber.d("initVertexArray(): vertex number = $numVertices")

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
            VERTEX_POS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_POS_SIZE * Float.SIZE_BYTES,
            mVertexArray.getFloatBuffer()
        )
        //将顶点颜色数据传送进渲染管线
        glVertexAttribPointer(
            maColorHandle,
            VERTEX_COLOR_SIZE,
            GL_FLOAT,
            false,
            VERTEX_COLOR_SIZE * Float.SIZE_BYTES,
            mColorArray.getFloatBuffer()
        )

        glEnableVertexAttribArray(maPositionHandle)     //启用顶点位置数据
        glEnableVertexAttribArray(maColorHandle)        //启用顶点着色数据
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

    companion object {

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3            //x,y,z
        private const val VERTEX_COLOR_SIZE = 4          //R,G,B,Alpha

    }
}