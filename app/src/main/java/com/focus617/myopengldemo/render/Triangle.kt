package com.focus617.myopengldemo.render

import android.opengl.GLES30
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer


class Triangle {

    private val vertexShaderCode =
        // 定义顶点着色器
        "#version 300 es \n" +
                "layout (location = 0) in vec3 aPos;" +
                "void main() {" +
                "   gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);" +
                "}"

    private val fragmentShaderCode = (
            "#version 300 es \n " +
                    "#ifdef GL_ES\n" +
                    "precision highp float;\n" +
                    "#endif\n" +

                    "out vec4 FragColor; " +

                    "void main() {" +
                    "  FragColor = vec4(1.0f,0.5f,0.2f,1.0f);" +
                    "}")


    private val mProgram: Int          // 着色器程序对象
    private val mVBOIds: IntBuffer    // 顶点缓存对象

    init {
        // 创建缓存，并绑定缓存类型
        mVBOIds = IntBuffer.allocate(1)
        GLES30.glGenBuffers(1, mVBOIds)
        Timber.i("VBO ID:"+ mVBOIds.get(0))

        // 顶点着色器
        var vertexShader = XGLRender.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        var success: IntBuffer = IntBuffer.allocate(1)
        GLES30.glGetShaderiv(vertexShader, GLES30.GL_COMPILE_STATUS, success)
        if (success.get(0) == 0) {
            Timber.e(GLES30.glGetShaderInfoLog(vertexShader));
            GLES30.glDeleteShader(vertexShader);
            vertexShader = 0
        }

        // 片段着色器
        var fragmentShader = XGLRender.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)
        GLES30.glGetShaderiv(fragmentShader, GLES30.GL_COMPILE_STATUS, success)
        if (success.get(0) == 0) {
            Timber.e(GLES30.glGetShaderInfoLog(fragmentShader))
            GLES30.glDeleteShader(fragmentShader)
            fragmentShader = 0
        }

        // 把着色器链接为一个着色器程序对象
        mProgram = GLES30.glCreateProgram()
        GLES30.glAttachShader(mProgram, vertexShader)
        GLES30.glAttachShader(mProgram, fragmentShader)
        GLES30.glLinkProgram(mProgram)

        GLES30.glGetProgramiv(mProgram, GLES30.GL_COMPILE_STATUS, success)
        if (success.get(0) == 0) {
            Timber.e(GLES30.glGetProgramInfoLog(mProgram))
            GLES30.glDeleteProgram(mProgram)
        }

        // 销毁不再需要的着色器对象
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOIds.get(0))
        // 把定义的顶点数据复制到缓存中
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            triangleCoords.size * 4,
            FloatBuffer.wrap(triangleCoords),
            GLES30.GL_STATIC_DRAW
        )

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        GLES30.glVertexAttribPointer(aPosLocation, 3, GLES30.GL_FLOAT, false, vertexStride, 0)

    }

    fun draw() {
        // 开放使能顶点数组
        GLES30.glEnableVertexAttribArray(aPosLocation);
        // 将程序添加到OpenGL ES环境
        GLES30.glUseProgram(mProgram)
        // 绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
        // 禁用顶点数组
        GLES30.glDisableVertexAttribArray(aPosLocation)
    }

    // 顶点数据集，及其属性
    companion object {
        // 顶点坐标维度
        internal const val COORDS_PER_VERTEX = 3

        // 连续的顶点属性组之间的间隔
        internal const val vertexStride = COORDS_PER_VERTEX * 4

        // aPos的位置偏移
        internal const val aPosLocation = 0

        // 三角形的顶点输入
        internal var triangleCoords = floatArrayOf(  // 按逆时针顺序
            0.0f, 0.622008459f, 0.0f,   // 上
            -0.5f, -0.311004243f, 0.0f, // 左下
            0.5f, -0.311004243f, 0.0f   // 右下
        )

        // 顶点的总数目
        internal val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    }
}