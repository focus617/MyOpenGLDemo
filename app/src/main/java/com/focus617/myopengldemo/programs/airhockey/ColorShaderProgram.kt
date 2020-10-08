package com.focus617.myopengldemo.programs.airhockey

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderConstants.U_COLOR
import com.focus617.myopengldemo.programs.ShaderConstants.U_MATRIX

class ColorShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.simple_vertex_shader,
    R.raw.simple_fragment_shader
) {

    // Uniform locations for the shader program.

    // Attribute locations for the shader program.
    fun getPositionAttributeLocation() = glGetAttribLocation(program, A_POSITION)

    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float) {
        // Pass the matrix into the shader program.
        setMatrix4fv(U_MATRIX, matrix)

        // Pass the color into the shader program.
        setVector4fv(U_COLOR, floatArrayOf(r, g, b, 1f),1)

    }
}
