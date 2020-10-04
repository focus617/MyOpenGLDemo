package com.focus617.myopengldemo.programs.airhockey

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_COLOR
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX

class ColorShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.simple_vertex_shader,
    R.raw.simple_fragment_shader
) {

    // Uniform locations for the shader program.
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uColorLocation = glGetUniformLocation(program, U_COLOR)

    // Attribute locations for the shader program.
    fun getPositionAttributeLocation() = glGetAttribLocation(program, A_POSITION)

//    fun getColorAttributeLocation() = glGetAttribLocation(program, A_COLOR)

    fun setUniforms(matrix: FloatArray?, r: Float, g: Float, b: Float) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        // Pass the color into the shader program.
        glUniform4f(uColorLocation, r, g, b, 1f)
    }
}
