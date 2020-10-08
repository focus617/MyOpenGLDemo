package com.focus617.myopengldemo.programs.particles

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderConstants.U_IT_MV_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_MV_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_POINT_LIGHT_COLORS
import com.focus617.myopengldemo.programs.ShaderConstants.U_POINT_LIGHT_POSITIONS
import com.focus617.myopengldemo.programs.ShaderConstants.U_TEXTURE_UNIT_1
import com.focus617.myopengldemo.programs.ShaderConstants.U_TEXTURE_UNIT_2
import com.focus617.myopengldemo.programs.ShaderConstants.U_VECTOR_TO_LIGHT

class HeightmapShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.heightmap_vertex_shader,
    R.raw.heightmap_fragment_shader
) {
    // Uniform locations for the shader program.
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
        setMatrix4fv(U_MV_MATRIX, mvMatrix)
        setMatrix4fv(U_IT_MV_MATRIX, it_mvMatrix)
        setMatrix4fv(U_MATRIX, mvpMatrix)

        setVector3fv(U_VECTOR_TO_LIGHT, vectorToDirectionalLight, 1)
        setVector4fv(U_POINT_LIGHT_POSITIONS, pointLightPositions, 3)
        setVector3fv(U_POINT_LIGHT_COLORS, pointLightColors, 3)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, grassTextureId)
        glUniform1i(uTextureUnitLocation1,0) // The 0 means "GL_TEXTURE0", or the first texture unit.

        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, stoneTextureId)
        glUniform1i(uTextureUnitLocation2,1) // The 1 means "GL_TEXTURE1", or the second texture unit.
    }

}