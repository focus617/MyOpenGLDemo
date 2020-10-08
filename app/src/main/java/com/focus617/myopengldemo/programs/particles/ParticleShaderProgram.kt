package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderConstants.A_COLOR
import com.focus617.myopengldemo.programs.ShaderConstants.A_DIRECTION_VECTOR
import com.focus617.myopengldemo.programs.ShaderConstants.A_PARTICLE_START_TIME
import com.focus617.myopengldemo.programs.ShaderConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_TEXTURE_UNIT
import com.focus617.myopengldemo.programs.ShaderConstants.U_TIME

class ParticleShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.particle_vertex_shader,
    R.raw.particle_fragment_shader
) {
    // Uniform locations for the shader program.

    // Attribute locations for the shader program.
    fun getPositionAttributeLocation(): Int = glGetAttribLocation(program, A_POSITION)
    fun getColorAttributeLocation(): Int = glGetAttribLocation(program, A_COLOR)
    fun getDirectionVectorAttributeLocation(): Int = glGetAttribLocation(program, A_DIRECTION_VECTOR)
    fun getParticleStartTimeAttributeLocation(): Int = glGetAttribLocation(program, A_PARTICLE_START_TIME)

    private val uTextureUnitLocation: Int = glGetUniformLocation(program, U_TEXTURE_UNIT)

    fun setUniforms(matrix: FloatArray, elapsedTime: Float){
        setMatrix4fv(U_MATRIX, matrix)
        setFloat(U_TIME, elapsedTime)
    }

    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        setMatrix4fv(U_MATRIX, matrix)
        setFloat(U_TIME, elapsedTime)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

}