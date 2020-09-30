package com.focus617.myopengldemo.render

import android.opengl.GLES31.*
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.sin


class Triangle : DrawingObject() {

    private val U_COLOR = "u_Color"

    // 定义顶点着色器
    // [mMVPMatrix] 模型视图投影矩阵
    private val vertexShaderCode =
        "#version 300 es \n" +
                "layout (location = 0) in vec3 aPos;" +
                "uniform mat4 uMVPMatrix;" +
                "void main() {" +
                "   gl_Position = uMVPMatrix * vec4(aPos.x, aPos.y, aPos.z, 1.0);" +
                "}"

    // 定义片段着色器
    private val fragmentShaderCode =
        "#version 300 es \n " +
                "#ifdef GL_ES\n" +
                "precision highp float;\n" +
                "#endif\n" +

                "out vec4 FragColor; " +
                "uniform vec4 u_Color; " +

                "void main() {" +
                "  FragColor = u_Color;" +
                "}"


    private val mProgramObject: Int     // 着色器程序对象
    private val mVBOIds: IntBuffer      // 顶点缓存对象

    init {
        mProgramObject = XGLRender.buildProgram(vertexShaderCode, fragmentShaderCode)

        // 创建缓存，并绑定缓存类型
        // mVBOIds[O] - used to store vertex attribute data
        mVBOIds = IntBuffer.allocate(1)
        glGenBuffers(1, mVBOIds)
        Timber.d("VBO ID: ${mVBOIds.get(0)}")
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))

        // 把定义的顶点数据复制到缓存中
        glBufferData(
            GL_ARRAY_BUFFER,
            triangleCoords.size * Float.SIZE_BYTES,
            FloatBuffer.wrap(triangleCoords),
            GL_STATIC_DRAW
        )
    }

    fun draw(mvpMatrix: FloatArray) {

        // 将程序添加到OpenGL ES环境
        glUseProgram(mProgramObject)

        // 获取模型视图投影矩阵的句柄
        val mMVPMatrixHandle = glGetUniformLocation(mProgramObject, "uMVPMatrix")
        // 将模型视图投影矩阵传递给顶点着色器
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        // 设置片元着色器使用的颜色
        setupColor(blink = true)

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))

        // 启用顶点数组
        glEnableVertexAttribArray(VERTEX_POS_INDEX)

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        // 目前只有一个顶点位置属性
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )

        // 图元装配，绘制三角形
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)

        // 禁用顶点数组
        glDisableVertexAttribArray(VERTEX_POS_INDEX)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun setupColor(blink: Boolean = false) {

        // 查询 uniform ourColor的位置值
        val fragmentColorLocation = glGetUniformLocation(mProgramObject, U_COLOR)
        if(blink){
            // 使用sin函数让颜色随时间在0.0到1.0之间改变
            val timeValue = System.currentTimeMillis()
            val greenValue = sin((timeValue / 300 % 50).toDouble()) / 2 + 0.5
            glUniform4f(fragmentColorLocation, greenValue.toFloat(), 0.5f, 0.2f, 1.0f)
        }
        else{
            glUniform4f(fragmentColorLocation, 1.0f, 0.5f, 0.2f, 1.0f)
        }
    }


    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_SIZE = 3          //x,y,and z
        internal const val VERTEX_NORMAL_SIZE = 3       //x,y,and z
        internal const val VERTEX_TEXCOORDO_SIZE = 2    //s and t
        internal const val VERTEX_TEXCOORD1_SIZE = 2    //s and t

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_NORMAL_INDEX = 1
        internal const val VERTEX_TEXCOORDO_INDEX = 2
        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_NORMAL_OFFSET = 3
        internal const val VERTEX_TEX_COORDO_OFFSET = 6
        internal const val VERTEX_TEX_COORD1_OFFSET = 8

        internal const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_SIZE
        // (VERTEX_POS_SIZE+ VERTEX_NORMAL_SIZE+ VERTEX_TEXCOORDO_SIZE+ VERTEX_TEXCOORD1_SIZE)

        // 一个等边三角形的顶点输入
        internal var triangleCoords = floatArrayOf(  // 按逆时针顺序
            0.0f, 0.622008459f, 0.0f,   // 上
            -0.5f, -0.311004243f, 0.0f, // 左下
            0.5f, -0.311004243f, 0.0f   // 右下
        )

        // 顶点的数量
        internal val vertexCount = triangleCoords.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES
    }
}