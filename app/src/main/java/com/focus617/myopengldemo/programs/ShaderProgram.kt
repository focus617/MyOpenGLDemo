package com.focus617.myopengldemo.programs

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.util.ShaderHelper
import com.focus617.myopengldemo.util.TextResourceReader

object ShaderProgramConstants {
    // Uniform constants
    const val U_MATRIX = "u_MVPMatrix"
    const val U_TEXTURE_UNIT = "u_TextureUnit"

    // Attribute constants
    const val A_POSITION = "a_Position"
    const val A_COLOR = "a_Color"
    const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
}

abstract class ShaderProgram protected constructor(
    context: Context,
    vertexShaderResourceId: Int,
    fragmentShaderResourceId: Int
) {
    // Shader program: compile the shaders and link the program.
    protected val program: Int = ShaderHelper.buildProgram(
        TextResourceReader.readTextFileFromResource(
            context, vertexShaderResourceId
        ),
        TextResourceReader.readTextFileFromResource(
            context, fragmentShaderResourceId
        )
    )

    fun useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program)
    }
}