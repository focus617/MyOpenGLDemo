package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderConstants
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_TEXTURE_UNIT

class SkyboxShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.skybox_vertex_shader,
    R.raw.skybox_fragment_shader
) {

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        setMatrix4fv(U_MATRIX, matrix)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId)
        setTexture(U_TEXTURE_UNIT,0)
    }

}
