package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.util.TextResourceReader
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import kotlin.math.sin

class AirHockey(context: Context) : DrawingObject() {

    private var mProgramObject: Int = 0    // 着色器程序对象
    private var mVAOId = IntBuffer.allocate(1)  // 顶点数组对象
    private var mVBOIds = IntBuffer.allocate(2)  // 顶点缓存对象

    init {
        setupProgram(context)
        setupVBO()
        setupVAO()
    }

    private fun setupProgram(context: Context) {
        // 顶点着色器
        val vertexShaderCode = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_vertex_shader)
        Timber.d("Load Vertex Shader Code:\n$vertexShaderCode\n")

        // 片段着色器
        val fragmentShaderCode = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_fragment_shader)
        Timber.d("Load Fragment Shader Code:\n$fragmentShaderCode\n")

        mProgramObject = XGLRender.buildProgram(vertexShaderCode, fragmentShaderCode)
    }

    private fun setupVBO() {
        // 创建缓存，并绑定缓存类型
        // mVBOIds[O] - used to store vertex attribute data
        // mVBOIds[l] - used to store element indices
        // allocate only on the first draw
        glGenBuffers(2, mVBOIds)
        Timber.d("VBO ID: $mVBOIds")

        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))
        // 把定义的顶点数据复制到缓存中
        glBufferData(
            GL_ARRAY_BUFFER,
            vertices.size * Float.SIZE_BYTES,
            FloatBuffer.wrap(vertices),
            GL_STATIC_DRAW
        )

        // bind buffer object for element indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(1))
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

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        // 顶点目前有两个属性：第一个是位置属性
        glBindBuffer(GL_ARRAY_BUFFER, mVBOIds.get(0))
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )
        // 顶点目前有两个属性：第二个是颜色属性
        glVertexAttribPointer(
            VERTEX_COLOR_INDEX,
            VERTEX_COLOR_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            (VERTEX_COLOR_OFFSET * Float.SIZE_BYTES)
        )

        // 启用顶点数组
        glEnableVertexAttribArray(VERTEX_POS_INDEX)
        glEnableVertexAttribArray(VERTEX_COLOR_INDEX)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(1))

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
        glDrawElements(GL_TRIANGLES, 12, GL_UNSIGNED_SHORT, 0)

        // 图元装配，绘制球桌中间的分隔线
        glDrawElements(GL_LINES, 2, GL_UNSIGNED_SHORT, 12)

        // 图元装配，绘制桌球 mallet blue
        glDrawElements(GL_POINTS, 1, GL_UNSIGNED_SHORT, 14)
        // 图元装配，绘制桌球 mallet red
        glDrawElements(GL_POINTS, 1, GL_UNSIGNED_SHORT, 15)

        // Reset to the default VAO
        glBindVertexArray(0)

    }

    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_COLOR_INDEX = 1
        internal const val VERTEX_NORMAL_INDEX = 1
        internal const val VERTEX_TEXCOORDO_INDEX = 2
        internal const val VERTEX_TEXCOORD1_INDEX = 3

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_SIZE = 2          // x,y
        internal const val VERTEX_COLOR_SIZE = 3        // r,g,b
        internal const val VERTEX_NORMAL_SIZE = 3       // x,y,z
        internal const val VERTEX_TEXCOORDO_SIZE = 2    // s,t
        internal const val VERTEX_TEXCOORD1_SIZE = 2    // s,t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_COLOR_OFFSET = 2
        internal const val VERTEX_NORMAL_OFFSET = 3
        internal const val VERTEX_TEX_COORDO_OFFSET = 6
        internal const val VERTEX_TEX_COORD1_OFFSET = 8

        internal const val VERTEX_ATTRIBUTE_SIZE= VERTEX_POS_SIZE + VERTEX_COLOR_SIZE
        // (VERTEX_POS_SIZE+ VERTEX_NORMAL_SIZE+ VERTEX_TEXCOORDO_SIZE+ VERTEX_TEXCOORD1_SIZE)

        // 球桌矩形的顶点
        internal var vertices = floatArrayOf(  // 按逆时针顺序
             0.0f,  0.00f, 1.0f, 1.0f, 1.0f,   // 0 middle
            -0.5f, -0.75f, 0.7f, 0.7f, 0.7f,   // 1 bottom left
             0.5f, -0.75f, 0.7f, 0.7f, 0.7f,   // 2 bottom right
             0.5f,  0.75f, 0.7f, 0.7f, 0.7f,   // 3 top right
            -0.5f,  0.75f, 0.7f, 0.7f, 0.7f,   // 4 top left
            -0.5f, -0.75f, 0.7f, 0.7f, 0.7f,   // 5 bottom left
            -0.5f,  0.00f, 1.0f, 0.0f, 0.0f,   // 6 middle line left
             0.5f,  0.00f, 1.0f, 0.0f, 0.0f,   // 7 middle line right
             0.0f, -0.55f, 0.0f, 0.0f, 1.0f,   // 8 Blue Mallets
             0.0f,  0.55f, 1.0f, 0.0f, 0.0f    // 9 Red  Mallets
        )

        // 顶点的数量
        internal val vertexCount = vertices.size / VERTEX_ATTRIBUTE_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES


        // 球桌矩形的顶点索引
        var indices = shortArrayOf(
            0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 5,
            6, 7, 8, 9
        )
    }
}