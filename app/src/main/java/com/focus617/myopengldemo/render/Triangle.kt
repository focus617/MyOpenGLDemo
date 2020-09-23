package com.focus617.myopengldemo.render

import android.opengl.GLES31
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.sin


class Triangle {

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
    private val fragmentShaderCode = (
            "#version 300 es \n " +
                    "#ifdef GL_ES\n" +
                    "precision highp float;\n" +
                    "#endif\n" +

                    "out vec4 FragColor; " +
                    "uniform vec4 outColor; " +

                    "void main() {" +
                    "  FragColor = outColor;" +
                    "}")


    private val mProgram: Int         // 着色器程序对象
    private val mVBOIds: IntBuffer    // 顶点缓存对象

    init {
        // 创建缓存，并绑定缓存类型
        mVBOIds = IntBuffer.allocate(1)
        GLES31.glGenBuffers(1, mVBOIds)
        Timber.d("VBO ID: ${mVBOIds.get(0)}")

        // 顶点着色器
        var vertexShader = XGLRender.loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode)
        var success: IntBuffer = IntBuffer.allocate(1)
        GLES31.glGetShaderiv(vertexShader, GLES31.GL_COMPILE_STATUS, success)
        if (success.get(0) == 0) {
            Timber.e(GLES31.glGetShaderInfoLog(vertexShader));
            GLES31.glDeleteShader(vertexShader);
            vertexShader = 0
        }

        // 片元着色器
        var fragmentShader = XGLRender.loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode)
        GLES31.glGetShaderiv(fragmentShader, GLES31.GL_COMPILE_STATUS, success)
        if (success.get(0) == 0) {
            Timber.e(GLES31.glGetShaderInfoLog(fragmentShader))
            GLES31.glDeleteShader(fragmentShader)
            fragmentShader = 0
        }

        // 把着色器链接为一个着色器程序对象
        mProgram = GLES31.glCreateProgram()
        GLES31.glAttachShader(mProgram, vertexShader)
        GLES31.glAttachShader(mProgram, fragmentShader)
        GLES31.glLinkProgram(mProgram)

        GLES31.glGetProgramiv(mProgram, GLES31.GL_COMPILE_STATUS, success)
        if (success.get(0) == 0) {
            Timber.e(GLES31.glGetProgramInfoLog(mProgram))
            GLES31.glDeleteProgram(mProgram)
        }

        // 销毁不再需要的着色器对象
        GLES31.glDeleteShader(vertexShader);
        GLES31.glDeleteShader(fragmentShader);

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mVBOIds.get(0))
        // 把定义的顶点数据复制到缓存中
        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            triangleCoords.size * 4,
            FloatBuffer.wrap(triangleCoords),
            GLES31.GL_STATIC_DRAW
        )

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        GLES31.glVertexAttribPointer(aPosLocation, 3, GLES31.GL_FLOAT, false, vertexStride, 0)

    }

    fun draw(mvpMatrix: FloatArray) {
        // 使用sin函数让颜色在0.0到1.0之间改变
        val timeValue = System.currentTimeMillis()
        val greenValue = sin((timeValue / 300 % 50).toDouble()) / 2 + 0.5

        // 启用顶点数组
        GLES31.glEnableVertexAttribArray(aPosLocation);

        // 将程序添加到OpenGL ES环境
        GLES31.glUseProgram(mProgram)

        // 获取模型视图投影矩阵的句柄
        val mMVPMatrixHandle = GLES31.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 将模型视图投影矩阵传递给着色器
        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        // 查询 uniform ourColor的位置值
        val vertexColorLocation = GLES31.glGetUniformLocation(mProgram, "outColor")
        GLES31.glUniform4f(vertexColorLocation, greenValue.toFloat(), 0f, 0f, 0f)

        // 绘制三角形
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, vertexCount)

        // 禁用顶点数组
        GLES31.glDisableVertexAttribArray(aPosLocation)
    }

    // 顶点数据集，及其属性
    companion object {
        // 顶点坐标维度
        internal const val COORDS_PER_VERTEX = 3

        // 连续的顶点属性组之间的间隔
        internal const val vertexStride = COORDS_PER_VERTEX * 4

        // aPos的位置偏移
        internal const val aPosLocation = 0

        // 一个等边三角形的顶点输入
        internal var triangleCoords = floatArrayOf(  // 按逆时针顺序
            0.0f, 0.622008459f, 0.0f,   // 上
            -0.5f, -0.311004243f, 0.0f, // 左下
            0.5f, -0.311004243f, 0.0f   // 右下
        )

        // 顶点的总数目
        internal val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    }
}