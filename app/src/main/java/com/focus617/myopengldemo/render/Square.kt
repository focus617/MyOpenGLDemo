package com.focus617.myopengldemo.render

import android.opengl.GLES31
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import kotlin.math.sin

class Square : DrawingObject() {
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
                    "uniform vec4 outColor; " +

                    "void main() {" +
                    "  FragColor = outColor;" +
                    "}"

    private val mProgramObject: Int     // 着色器程序对象
    private val mVBOIds: IntBuffer      // 顶点缓存对象

    init {
        // 创建缓存，并绑定缓存类型
        mVBOIds = IntBuffer.allocate(2)
        // mVBOIds[O] - used to store vertex attribute data
        // mVBOIds[l] - used to store element indices
        GLES31.glGenBuffers(2, mVBOIds)
        Timber.d("VBO ID: $mVBOIds")

        var success: IntBuffer = IntBuffer.allocate(1)

        // 顶点着色器
        var vertexShader = XGLRender.loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode)

        // 片元着色器
        var fragmentShader = XGLRender.loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 把着色器链接为一个着色器程序对象
        mProgramObject = GLES31.glCreateProgram()
        GLES31.glAttachShader(mProgramObject, vertexShader)
        GLES31.glAttachShader(mProgramObject, fragmentShader)
        GLES31.glLinkProgram(mProgramObject)

        GLES31.glGetProgramiv(mProgramObject, GLES31.GL_LINK_STATUS, success)
        if (success.get(0) == 0) {
            Timber.e(GLES31.glGetProgramInfoLog(mProgramObject))
            GLES31.glDeleteProgram(mProgramObject)
        } else {
            Timber.d("GLProgram $mProgramObject is ready.")
        }

        // 销毁不再需要的着色器对象
        GLES31.glDeleteShader(vertexShader)
        GLES31.glDeleteShader(fragmentShader)
        // 释放着色器编译器使用的资源
        GLES31.glReleaseShaderCompiler()


        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mVBOIds.get(0))
        // 把定义的顶点数据复制到缓存中
        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            vertexCoords.size * Float.SIZE_BYTES,
            FloatBuffer.wrap(vertexCoords),
            GLES31.GL_STATIC_DRAW
        )

        // bind buffer object for element indices
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(1))
        GLES31.glBufferData(
            GLES31.GL_ELEMENT_ARRAY_BUFFER,
            indices.size * Short.SIZE_BYTES,
            ShortBuffer.wrap(indices),
            GLES31.GL_STATIC_DRAW)

    }

    fun draw(mvpMatrix: FloatArray) {

        // 将程序添加到OpenGL ES环境
        GLES31.glUseProgram(mProgramObject)

        // 获取模型视图投影矩阵的句柄
        val mMVPMatrixHandle = GLES31.glGetUniformLocation(mProgramObject, "uMVPMatrix")
        // 将模型视图投影矩阵传递给顶点着色器
        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        // 设置片元着色器使用的颜色
        //setupBlinkColor()
        setupSolidColor()

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mVBOIds.get(0))
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(1))

        // 启用顶点数组
        GLES31.glEnableVertexAttribArray(Triangle.VERTEX_POS_INDEX);

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        // 目前只有一个顶点位置属性
        GLES31.glVertexAttribPointer(
            Triangle.VERTEX_POS_INDEX,
            Triangle.VERTEX_POS_SIZE,
            GLES31.GL_FLOAT,
            false,
            Triangle.VERTEX_STRIDE,
            Triangle.VERTEX_POS_OFFSET
        )

        // 图元装配，绘制三角形
        GLES31.glDrawElements(GLES31.GL_TRIANGLES, indices.size,
            GLES31.GL_UNSIGNED_SHORT, 0)

        // 禁用顶点数组
        GLES31.glDisableVertexAttribArray(Triangle.VERTEX_POS_INDEX)

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    private fun setupBlinkColor() {
        // 使用sin函数让颜色随时间在0.0到1.0之间改变
        val timeValue = System.currentTimeMillis()
        val greenValue = sin((timeValue / 300 % 50).toDouble()) / 2 + 0.5

        // 查询 uniform ourColor的位置值
        val fragmentColorLocation = GLES31.glGetUniformLocation(mProgramObject, "outColor")
        GLES31.glUniform4f(fragmentColorLocation, greenValue.toFloat(), 0.5f, 0.2f, 1.0f)
    }

    private fun setupSolidColor() {

        // 查询 uniform ourColor的位置值
        val fragmentColorLocation = GLES31.glGetUniformLocation(mProgramObject, "outColor")
        GLES31.glUniform4f(fragmentColorLocation, 1.0f, 0.5f, 0.2f, 1.0f)
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

        // 正方形的顶点
        internal var vertexCoords = floatArrayOf(  // 按逆时针顺序
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,  // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f     // top right
        )
        // 顶点的数量
        internal val vertexCount = vertexCoords.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES


        // 顶点索引
        var indices = shortArrayOf(
            0, 1, 2, 0, 2, 3
        )
    }
}