package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.airhockey.Mallet
import com.focus617.myopengldemo.objects.airhockey.Table
import com.focus617.myopengldemo.programs.airhockey.ColorShaderProgram
import com.focus617.myopengldemo.programs.airhockey.TextureShaderProgram
import com.focus617.myopengldemo.util.MatrixHelper
import com.focus617.myopengldemo.util.TextureHelper
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 拆分类之后（第7章），按照ES 2.0实现
 */
class AirHockeyRendererEs2(override val context: Context) : XGLRenderer(context) {

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture = 0

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)

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
        MatrixHelper.perspectiveM(projectionMatrix, 45f, aspect,1f, 10f)

        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.8f)
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(glUnused: GL10) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT)

        // Draw the table.
        textureProgram.useProgram()
        textureProgram.setUniforms(projectionMatrix, texture)
        table.bindData()
        table.draw()

        // Draw the mallets.
        colorProgram.useProgram()
        colorProgram.setUniforms(projectionMatrix, 1f, 0f, 0f)
        mallet.bindData()
        mallet.draw()
    }
}