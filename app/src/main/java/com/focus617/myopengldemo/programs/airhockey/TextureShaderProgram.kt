package com.focus617.myopengldemo.programs.airhockey

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderConstants.U_MVP_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_TEXTURE_UNIT

class TextureShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.texture_vertex_shader,
    R.raw.texture_fragment_shader
) {

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        // Pass the matrix into the shader program.
        setMatrix4fv(U_MVP_MATRIX, matrix)

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0)

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId)

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        setTexture(U_TEXTURE_UNIT,0)
    }

}


