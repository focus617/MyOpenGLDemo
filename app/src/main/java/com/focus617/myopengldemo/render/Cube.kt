package com.focus617.myopengldemo.render

import android.opengl.GLES31.*
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import kotlin.math.sin

class Cube : DrawingObject() {

    // 定义顶点着色器
    // [mMVPMatrix] 模型视图投影矩阵
    private val vertexShaderCode =
        ("#version 300 es \n" +
                "layout (location = 0) in vec3 aPos;" +
                "layout (location = 1) in vec4 a_Color;" +
                "uniform mat4 uMVPMatrix;" +

                "out vec4 v_Color;"+

                "void main() {" +
                "   v_Color = a_Color;" +
                "   gl_Position = uMVPMatrix * vec4(aPos.x, aPos.y, aPos.z, 1.0);" +
                "}")

    // 定义片段着色器
    private val fragmentShaderCode =
        ("#version 300 es \n " +
                "#ifdef GL_ES\n" +
                "precision highp float;\n" +
                "#endif\n" +

                "in vec4 v_Color; " +
                "out vec4 FragColor; " +

                "void main() {" +
                "  FragColor = v_Color;" +
                "}")

    private var mProgramObject: Int = 0    // 着色器程序对象
    private var mVAOId  = IntBuffer.allocate(1)  // 顶点数组对象
    private var mVBOIds = IntBuffer.allocate(3)  // 顶点缓存对象

    init {
        setupProgram()
        setupVBO()
        setupVAO()
    }

    private fun setupProgram() {
        mProgramObject = XGLRender.buildProgram(vertexShaderCode, fragmentShaderCode)
    }

    private fun setupVBO() {
        // 创建缓存，并绑定缓存类型
        // mVBOIds[O] - used to store vertex attribute data
        // mVBOIds[l] - used to store vertex color
        // mVBOIds[2] - used to store element indices
        // allocate only on the first draw
        glGenBuffers(3, mVBOIds)
        Timber.d("VBO ID: $mVBOIds")

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))
        // 把定义的顶点数据复制到缓存中
        glBufferData(
            GL_ARRAY_BUFFER,
            vertices.size * Float.SIZE_BYTES,
            FloatBuffer.wrap(vertices),
            GL_STATIC_DRAW
        )

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(1))
        // 把定义的顶点数据复制到缓存中
        glBufferData(
            GL_ARRAY_BUFFER,
            colors.size * Float.SIZE_BYTES,
            FloatBuffer.wrap(colors),
            GL_STATIC_DRAW
        )

        // bind buffer object for element indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(2))
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER,
            indices.size * Short.SIZE_BYTES,
            ShortBuffer.wrap(indices),
            GL_STATIC_DRAW
        )
    }

    private fun setupVAO() {
        //Generate VAO ID
        glGenVertexArrays(1, mVAOId)

        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(mVAOId.get(0))

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(2))

        // 启用顶点数组
        glEnableVertexAttribArray(VERTEX_POS_INDEX)
        glEnableVertexAttribArray(VERTEX_COLOR_INDEX)

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        // 顶点的位置属性
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )

        // 顶点的颜色属性
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(1))
        glVertexAttribPointer(
            VERTEX_COLOR_INDEX,
            VERTEX_COLOR_SIZE,
            GL_FLOAT,
            false,
            VERTEX_COLOR_STRIDE,
            0
        )

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    fun draw(mvpMatrix: FloatArray) {

        // 将程序添加到OpenGL ES环境
        glUseProgram(mProgramObject)

        // 获取模型视图投影矩阵的句柄
        val mMVPMatrixHandle = glGetUniformLocation(mProgramObject, "uMVPMatrix")
        // 将模型视图投影矩阵传递给顶点着色器
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(mVAOId.get(0))

        // 图元装配，绘制三角形
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_SHORT, 0)

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_NORMAL_INDEX = 1
        internal const val VERTEX_TEXCOORDO_INDEX = 2
        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_SIZE = 3          //x,y,and z
        internal const val VERTEX_NORMAL_SIZE = 3       //x,y,and z
        internal const val VERTEX_TEXCOORDO_SIZE = 2    //s and t
        internal const val VERTEX_TEXCOORD1_SIZE = 2    //s and t

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
        internal var vertices = floatArrayOf(  // 按逆时针顺序
            //正面矩形
             0.25f,  0.25f, 0.0f, //V0
            -0.75f,  0.25f, 0.0f, //V1
            -0.75f, -0.75f, 0.0f, //V2
             0.25f, -0.75f, 0.0f, //V3

            //背面矩形
             0.75f, -0.25f, 0.0f, //V4
             0.75f,  0.75f, 0.0f, //V5
            -0.25f,  0.75f, 0.0f, //V6
            -0.25f, -0.25f, 0.0f  //V7
        )

        // 顶点的数量
        internal val vertexCount = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES


        // 立方体的顶点索引
        var indices = shortArrayOf(
            //背面
            5, 6, 7, 5, 7, 4,
            //左侧
            6, 1, 2, 6, 2, 7,
            //底部
            4, 7, 2, 4, 2, 3,
            //顶面
            5, 6, 7, 5, 7, 4,
            //右侧
            5, 0, 3, 5, 3, 4,
            //正面
            0, 1, 2, 0, 2, 3
        )

        // 立方体的顶点颜色
        // 顶点坐标的每个属性的Size
        internal const val VERTEX_COLOR_INDEX = 1
        internal const val VERTEX_COLOR_SIZE = 4          // r,g,b,alpha
        internal const val VERTEX_COLOR_OFFSET = 0
        internal const val VERTEX_COLOR_STRIDE = VERTEX_COLOR_SIZE * Float.SIZE_BYTES

        val colors = floatArrayOf(
            0.3f, 0.4f, 0.5f, 1f,  //V0
            0.3f, 0.4f, 0.5f, 1f,  //V1
            0.3f, 0.4f, 0.5f, 1f,  //V2
            0.3f, 0.4f, 0.5f, 1f,  //V3
            0.6f, 0.5f, 0.4f, 1f,  //V4
            0.6f, 0.5f, 0.4f, 1f,  //V5
            0.6f, 0.5f, 0.4f, 1f,  //V6
            0.6f, 0.5f, 0.4f, 1f   //V7
        )
    }
}