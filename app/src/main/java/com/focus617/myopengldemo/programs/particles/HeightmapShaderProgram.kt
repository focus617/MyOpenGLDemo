package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderProgramConstants.A_POSITION
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_IT_MV_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MV_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_POINT_LIGHT_COLORS
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_POINT_LIGHT_POSITIONS
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_VECTOR_TO_LIGHT
import com.focus617.myopengldemo.util.Geometry.Companion.Vector

class HeightmapShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.heightmap_vertex_shader,
    R.raw.heightmap_fragment_shader
) {
    // Uniform locations for the shader program.
    private val uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX)
    private val uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX)
    private val uMVPMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)

    private val uVectorToLightLocation: Int = glGetUniformLocation(program, U_VECTOR_TO_LIGHT)
    private val uPointLightPositionsLocation =
        glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS)
    private val uPointLightColorsLocation = glGetUniformLocation(program, U_POINT_LIGHT_COLORS)

    fun setUniforms(
        mvMatrix: FloatArray,
        it_mvMatrix: FloatArray,
        mvpMatrix: FloatArray,
        vectorToDirectionalLight: FloatArray,
        pointLightPositions: FloatArray,
        pointLightColors: FloatArray
    ) {
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0)
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0)
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)

        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0)
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0)
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0)
    }


}

