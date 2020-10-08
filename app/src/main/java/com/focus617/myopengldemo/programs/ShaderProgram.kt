package com.focus617.myopengldemo.programs

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.util.ShaderHelper
import com.focus617.myopengldemo.util.TextResourceReader

object ShaderProgramConstants {
    // Uniform constants
    const val U_MATRIX = "u_MVPMatrix"
    const val U_COLOR = "u_Color"
    const val U_TEXTURE_UNIT = "u_TextureUnit"
    const val U_TEXTURE_UNIT_1 = "u_TextureUnit1"
    const val U_TEXTURE_UNIT_2 = "u_TextureUnit2"
    const val U_TIME = "u_Time"
    const val U_VECTOR_TO_LIGHT = "u_VectorToLight"
    const val U_MV_MATRIX = "u_MVMatrix"
    const val U_IT_MV_MATRIX = "u_IT_MVMatrix"
    const val U_POINT_LIGHT_POSITIONS = "u_PointLightPositions"
    const val U_POINT_LIGHT_COLORS = "u_PointLightColors"

    // Attribute constants
    const val A_POSITION = "a_Position"
    const val A_COLOR = "a_Color"
    const val A_NORMAL = "a_Normal"
    const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    const val A_DIRECTION_VECTOR = "a_DirectionVector"
    const val A_PARTICLE_START_TIME = "a_ParticleStartTime"
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

    // 使用/激活程序
    fun useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program)
    }

    // uniform工具函数
    fun setBool( attributeName:String, bool: Boolean )
    {
        val value = if(bool) 1 else 0
        glUniform1i(glGetUniformLocation(program, attributeName), value)
    }

    fun setInt( attributeName:String, value: Int)
    {
        glUniform1i(glGetUniformLocation(program, attributeName), value )
    }

    fun setFloat( attributeName:String, value: Float)
    {
        glUniform1f(glGetUniformLocation(program, attributeName), value )
    }

    fun setVector3fv( attributeName:String, vector: FloatArray, count: Int)
    {
        glUniform3fv(glGetUniformLocation(program, attributeName),
            count,  vector, 0 )
    }

    fun setVector4fv( attributeName:String, vector: FloatArray, count: Int)
    {
        glUniform4fv(glGetUniformLocation(program, attributeName),
            count, vector, 0 )
    }

    fun setMatrix4fv( attributeName:String, matrix: FloatArray)
    {
        glUniformMatrix4fv(glGetUniformLocation(program, attributeName),
            1, false, matrix, 0 )
    }
}
