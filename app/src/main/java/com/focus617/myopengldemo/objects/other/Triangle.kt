package com.focus617.myopengldemo.objects.other

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.VertexArray
import com.focus617.myopengldemo.base.objectbuilder.MeshObject


class Triangle(context: Context) : MeshObject(context) {

    init{
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()
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
            1f, 1f, 1f,
            0f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f
        )

        // Transfer data to native memory.
        mColorArray = VertexArray(colors)

        // Reset 缓冲区起始位置 to origin offset
        mColorArray.position(0)
    }

    override fun draw() {

        //将顶点位置数据传送进渲染管线
        glVertexAttribPointer(
            maPositionHandle,
            3,
            GL_FLOAT,
            false,
            3 * 4,
            mColorArray.getFloatBuffer()
        )
        //将顶点颜色数据传送进渲染管线
        GLES30.glVertexAttribPointer(
            maColorHandle,
            4,
            GL_FLOAT,
            false,
            4 * 4,
            mColorArray.getFloatBuffer()
        )
        glEnableVertexAttribArray(maPositionHandle) //启用顶点位置数据

        glEnableVertexAttribArray(maColorHandle) //启用顶点着色数据

        //绘制三角形
        glDrawArrays(GL_TRIANGLES, 0, numVertices)

    }

}