package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31.GL_COLOR_BUFFER_BIT
import android.opengl.GLES31.glClear
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.airhockey.Mallet
import com.focus617.myopengldemo.objects.airhockey.Puck
import com.focus617.myopengldemo.objects.airhockey.Table
import com.focus617.myopengldemo.programs.ColorShaderProgram
import com.focus617.myopengldemo.programs.TextureShaderProgram
import com.focus617.myopengldemo.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 拆分类之后（第7章），按照ES 3.0 VBO, VAO和 Element改进的实现
 */
class AirHockeyRendererEs3(override val context: Context) : XGLRenderer(context) {

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture = 0

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        super.onSurfaceCreated(glUnused, config)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)

        table.bindDataEs3(textureProgram)
        mallet.bindDataEs3(colorProgram)
    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT)

        // 视图转换：Multiply the view and projection matrices together
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Draw the table.
        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(mMVPMatrix, texture)
        table.drawEs3()

        // Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f)
        colorProgram.useProgram()
        colorProgram.setUniforms(mMVPMatrix, 1f, 0f, 0f)
        mallet.drawEs3()

        positionObjectInScene(0f, mallet.height / 2f, 0.4f)
        colorProgram.setUniforms(mMVPMatrix, 0f, 0f, 1f)
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        mallet.drawEs3()

        // Draw the puck.
        positionObjectInScene(0f, puck.height / 2f, 0f)
        colorProgram.setUniforms(mMVPMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()
    }

    private fun positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.rotateM(mModelMatrix, 0, -90f, 1f, 0f, 0f)
        Matrix.multiplyMM(
            mMVPMatrix, 0, mViewProjectionMatrix,
            0, mModelMatrix, 0
        )
    }
}