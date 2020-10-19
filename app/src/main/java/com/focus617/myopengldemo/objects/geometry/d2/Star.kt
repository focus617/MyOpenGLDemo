package com.focus617.myopengldemo.objects.geometry.d2

import android.content.Context
import android.opengl.GLES31
import com.focus617.myopengldemo.base.objectbuilder.MeshObject
import com.focus617.myopengldemo.base.objectbuilder.VertexArray
import timber.log.Timber

class Star(
    context: Context,
    val radius: Float,
    val R: Float,
    val z: Float,
    val angleNum: Int = 5
) : MeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    override fun initVertexArray() {
        initVertices(radius, R, z)      //初始化顶点坐标
        initColors()                    //初始化顶点颜色
    }

    //初始化顶点坐标
    private fun initVertices(r: Float, R: Float, z: Float) {

        val UNIT_SIZE = 1f

        val vertexList = ArrayList<Float>()
        val tempAngle: Int = 360 / angleNum
        val t = z + UNIT_SIZE * 0.12f

        for (angle in 0 until 360 step tempAngle)//循环生成构成六角形各三角形的顶点坐标
        {
            //第一个三角形
            //第一个点的x、y、z坐标
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(t)
            //第二个点的x、y、z坐标
            vertexList.add((R * UNIT_SIZE * Math.cos(Math.toRadians(angle.toDouble()))).toFloat())
            vertexList.add((R * UNIT_SIZE * Math.sin(Math.toRadians(angle.toDouble()))).toFloat())
            vertexList.add(z)
            //第三个点的x、y、z坐标
            vertexList.add((r * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add((r * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add(z)

            //第二个三角形
            //第一个中心点的x、y、z坐标
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(t)
            //第二个点的x、y、z坐标
            vertexList.add((r * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add((r * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add(z)
            //第三个点的x、y、z坐标
            vertexList.add((R * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle).toDouble()))).toFloat())
            vertexList.add((R * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle).toDouble()))).toFloat())
            vertexList.add(z)
        }

        // 将构造的顶点列表转存为顶点数组
        numVertices = vertexList.size / 3
        val vertexArray = FloatArray(vertexList.size)    //顶点坐标数组
        for (i in 0 until numVertices) {
            vertexArray[i * 3] = vertexList[i * 3]
            vertexArray[i * 3 + 1] = vertexList[i * 3 + 1]
            vertexArray[i * 3 + 2] = vertexList[i * 3 + 2]
            Timber.d("$i:(${vertexArray[i * 3]},${vertexArray[i * 3 + 1]},${vertexArray[i * 3 + 2]})")
        }
        //顶点坐标数据的初始化
        mVertexArray = VertexArray(vertexArray)
        Timber.d("initVertexArray(): vertex number = $numVertices")

        // 将顶点数据存入缓冲区
        setupVertices()
    }

    //初始化顶点颜色
    private fun initColors() {
        //顶点着色数据的初始化
        val colorArray = FloatArray(numVertices * 4)    //顶点着色数据的初始化
        for (i in 0 until numVertices) {
            if (i % 3 == 0) {               //中心点为白色，RGBA 4个通道[1,1,1,0]
                colorArray[i * 4] = 1f
                colorArray[i * 4 + 1] = 1f
                colorArray[i * 4 + 2] = 1f
                colorArray[i * 4 + 3] = 0f
            } else {                        //边上的点为红色，RGBA 4个通道
                // 原为淡蓝色 [0.45,0.75,0.75,0]
                colorArray[i * 4] = 1.0f
                colorArray[i * 4 + 1] = 0f
                colorArray[i * 4 + 2] = 0f
                colorArray[i * 4 + 3] = 0f
            }
        }
        // Transfer color data to native memory.
        mColorArray = VertexArray(colorArray)

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mColorId)
        // Reset 缓冲区起始位置 to origin offset
        mColorArray.position(0)
        // Transfer data from native memory to the GPU buffer.
        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            mColorArray.capacity() * Float.SIZE_BYTES,
            mColorArray.getFloatBuffer(),
            GLES31.GL_STATIC_DRAW
        )
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
    }


    override fun initShader() {
        //自定义渲染管线程序
        mProgram = SimpleShapeShaderProgram(context)
        bindData()
    }

    override fun bindData() {
        // Bind the VAO and then set up the vertex attributes
        GLES31.glBindVertexArray(mVaoId)

        // Bind VBO buffer for Vertex Position
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mVertexId)
        // 设置顶点属性
        GLES31.glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GLES31.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )
        // 启用顶点属性
        GLES31.glEnableVertexAttribArray(VERTEX_POS_INDEX)

        // Bind VBO buffer for Vertex Color
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mColorId)
        // 设置顶点属性
        GLES31.glVertexAttribPointer(
            VERTEX_COLOR_INDEX,
            VERTEX_COLOR_SIZE,
            GLES31.GL_FLOAT,
            false,
            VERTEX_COLOR_SIZE * Float.SIZE_BYTES,
            VERTEX_COLOR_OFFSET
        )
        // 启用顶点属性
        GLES31.glEnableVertexAttribArray(VERTEX_COLOR_INDEX)

        // Reset to the default VAO
        GLES31.glBindVertexArray(0)
    }

    override fun draw() {
        // 将程序添加到OpenGL ES环境
        mProgram.use()

        // Bind the VAO and then draw with VAO settings
        GLES31.glBindVertexArray(mVaoId)

        // 图元装配，绘制三角形
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, numVertices)

        // Reset to the default VAO
        GLES31.glBindVertexArray(0)

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {
        mProgram.use()
        (mProgram as SimpleShapeShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix
        )
    }


    // 顶点数据集，及其属性
    companion object {
        // 假定每个顶点有4个顶点属性一位置、法线和两个纹理坐标

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3          //x,y,and z
        private const val VERTEX_COLOR_SIZE = 4        //R,G,B,Alpha

        // 顶点坐标的每个属性的Index
        private const val VERTEX_POS_INDEX = 0
        private const val VERTEX_COLOR_INDEX = 1

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        private const val VERTEX_POS_OFFSET = 0
        private const val VERTEX_COLOR_OFFSET = 0

        private const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_SIZE

        // 连续的顶点属性组之间的间隔
        private const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }
}

