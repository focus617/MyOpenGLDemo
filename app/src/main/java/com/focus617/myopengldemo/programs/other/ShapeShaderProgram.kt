package com.focus617.myopengldemo.programs.other

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_COLOR
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_DIRECTION_VECTOR
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_PARTICLE_START_TIME
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_TEXTURE_UNIT
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_TIME

class ShapeShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.shape_vertex_shader,
    R.raw.shape_fragment_shader
) {
    // Uniform locations for the shader program.
    private val uTextureUnitLocation: Int = glGetUniformLocation(program, U_TEXTURE_UNIT)

    // Attribute locations for the shader program.

    fun setUniforms(matrix: FloatArray, textureId: Int){
        setMatrix4fv(U_MATRIX, matrix)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }
}