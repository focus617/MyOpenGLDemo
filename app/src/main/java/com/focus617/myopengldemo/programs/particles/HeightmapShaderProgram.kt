package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_VECTOR_TO_LIGHT
import com.focus617.myopengldemo.util.Geometry.Companion.Vector

class HeightmapShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.heightmap_vertex_shader,
    R.raw.heightmap_fragment_shader
) {
    // Uniform locations for the shader program.
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uVectorToLightLocation : Int = glGetUniformLocation(program, U_VECTOR_TO_LIGHT)

    fun getPositionAttributeLocation(): Int = glGetAttribLocation(program, A_POSITION)

    fun setUniforms(
        matrix: FloatArray,
        vectorToLight: Vector
    ) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform3f(uVectorToLightLocation, vectorToLight.x, vectorToLight.y, vectorToLight.z)
    }

}

