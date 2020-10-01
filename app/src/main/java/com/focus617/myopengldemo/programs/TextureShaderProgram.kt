package com.focus617.myopengldemo.programs

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_TEXTURE_COORDINATES
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_TEXTURE_UNIT

class TextureShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.texture_vertex_shader,
    R.raw.texture_fragment_shader
) {
    // Retrieve uniform locations for the shader program.
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uTextureUnitLocation: Int = glGetUniformLocation(program, U_TEXTURE_UNIT)

    // Retrieve attribute locations for the shader program.
    fun getPositionAttributeLocation(): Int = glGetAttribLocation(program, A_POSITION)
    fun getTextureCoordinatesAttributeLocation(): Int =
        glGetAttribLocation(program, A_TEXTURE_COORDINATES)

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0)

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId)

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0)
    }

}


