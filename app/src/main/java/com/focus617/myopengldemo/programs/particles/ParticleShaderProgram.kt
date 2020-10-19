package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MVP_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_TEXTURE_UNIT
import com.focus617.myopengldemo.base.program.ShaderConstants.U_TIME

class ParticleShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.particle_vertex_shader,
    R.raw.particle_fragment_shader
) {

    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        setMatrix4fv(U_MVP_MATRIX, matrix)
        setFloat(U_TIME, elapsedTime)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        setTexture(U_TEXTURE_UNIT,0)
    }

}