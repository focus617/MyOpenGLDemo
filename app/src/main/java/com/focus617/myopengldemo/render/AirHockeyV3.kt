package com.focus617.myopengldemo.render

import android.content.Context
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.airhockey.Mallet
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

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture = 0

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        super.onSurfaceCreated(glUnused,config)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)

        table.bindDataEs3(textureProgram)
        mallet.bindDataEs3(colorProgram)
    }

    override fun onDrawShape() {

        // Draw the table.
        textureProgram.useProgram()
        textureProgram.setUniforms(mMVPMatrix, texture)
        table.drawEs3()

        // Draw the mallets.
        colorProgram.useProgram()
        colorProgram.setUniforms(mMVPMatrix, 1f, 0f, 0f)
        mallet.drawEs3()
    }
}