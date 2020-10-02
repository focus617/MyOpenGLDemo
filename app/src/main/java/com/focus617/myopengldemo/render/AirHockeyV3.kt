package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.airhockey.Mallet
import com.focus617.myopengldemo.objects.airhockey.Table
import com.focus617.myopengldemo.programs.ColorShaderProgram
import com.focus617.myopengldemo.programs.TextureShaderProgram
import com.focus617.myopengldemo.util.MatrixHelper
import com.focus617.myopengldemo.util.TextureHelper
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 拆分类之后（第7章），准备按照ES 3.0 VBO, VAO和 Element改进的实现
 */
class AirHockeyRendererV3(override val context: Context) : XGLRenderer(context) {

    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture = 0

    // 处理旋转
    private fun setupRotation() {
        // 进行旋转变换
        Matrix.rotateM(mViewMatrix, 0, getAngle(), 1.0f, 0f, 0f)
    }

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        table = Table()
        mallet = Mallet()

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {

        // 设置渲染的OpenGL场景（视口）的位置和大小
        Timber.d("width = $width, height = $height")

        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        // 计算透视投影矩阵 (Project Matrix)，而后将应用于onDrawFrame（）方法中的对象坐标
        val aspect: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f)

    }

    override fun onDrawFrame(glUnused: GL10) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT)

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3.5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // 处理旋转
        setupRotation()

        // 视图转换：计算模型视图投影矩阵MVPMatrix，该矩阵可以将模型空间的坐标转换为归一化设备空间坐标
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Draw the table.
        textureProgram.useProgram()
        textureProgram.setUniforms(mMVPMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        // Draw the mallets.
        colorProgram.useProgram()
        colorProgram.setUniforms(mMVPMatrix)
        mallet.bindData(colorProgram)
        mallet.draw()
    }
}