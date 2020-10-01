package com.focus617.myopengldemo.programs

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_COLOR
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX

class ColorShaderProgram(context: Context?) :
    ShaderProgram(
        context!!,
        R.raw.simple_vertex_shader,
        R.raw.simple_fragment_shader
    ) {

    // Uniform locations for the shader program.
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)

    // Attribute locations for the shader program.
    private val positionAttributeLocation = glGetAttribLocation(program, A_POSITION)
    private val colorAttributeLocation = glGetAttribLocation(program, A_COLOR)

    fun setUniforms(matrix: FloatArray?) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }
}
