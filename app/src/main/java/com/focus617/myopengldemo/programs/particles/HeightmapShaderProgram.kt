package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_IT_MV_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_MV_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_POINT_LIGHT_COLORS
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_POINT_LIGHT_POSITIONS
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_TEXTURE_UNIT_1
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_TEXTURE_UNIT_2
import com.focus617.myopengldemo.programs.ShaderProgramConstants.U_VECTOR_TO_LIGHT

class HeightmapShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.heightmap_vertex_shader,
    R.raw.heightmap_fragment_shader
) {
    // Uniform locations for the shader program.
    private var uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX)
    private var uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX)
    private var uMVPMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)

    private var uVectorToLightLocation: Int = glGetUniformLocation(program, U_VECTOR_TO_LIGHT)
    private var uPointLightPositionsLocation =
        glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS)
    private var uPointLightColorsLocation = glGetUniformLocation(program, U_POINT_LIGHT_COLORS)

    private var uTextureUnitLocation1 = glGetUniformLocation(program, U_TEXTURE_UNIT_1)
    private var uTextureUnitLocation2 = glGetUniformLocation(program, U_TEXTURE_UNIT_2)

    fun setUniforms(
        mvMatrix: FloatArray,
        it_mvMatrix: FloatArray,
        mvpMatrix: FloatArray,
        vectorToDirectionalLight: FloatArray,
        pointLightPositions: FloatArray,
        pointLightColors: FloatArray,
        grassTextureId: Int,
        stoneTextureId: Int
    ) {
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0)
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0)
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)

        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0)
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0)
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, grassTextureId)
        glUniform1i(uTextureUnitLocation1,0) // The 0 means "GL_TEXTURE0", or the first texture unit.
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, stoneTextureId)
        glUniform1i(uTextureUnitLocation2,1) // The 1 means "GL_TEXTURE1", or the second texture unit.
    }

}