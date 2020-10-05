package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES20
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_TEXTURE_UNIT

class SkyboxShaderProgram(context: Context) :
    ShaderProgram(
        context,
        R.raw.skybox_vertex_shader,
        R.raw.skybox_fragment_shader
    ) {
    private val uMatrixLocation: Int = GLES20.glGetUniformLocation(program, U_MATRIX)
    private val uTextureUnitLocation: Int = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT)

   fun getPositionAttributeLocation(): Int = GLES20.glGetAttribLocation(program, A_POSITION)

    fun setUniforms(matrix: FloatArray?, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId)
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }

}
