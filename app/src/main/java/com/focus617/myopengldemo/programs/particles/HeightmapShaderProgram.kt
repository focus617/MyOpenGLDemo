package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX

class HeightmapShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.heightmap_vertex_shader,
    R.raw.heightmap_fragment_shader
) {
    // Uniform locations for the shader program.
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)

    fun getPositionAttributeLocation(): Int = glGetAttribLocation(program, A_POSITION)

    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

}
