package com.focus617.myopengldemo.programs

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import com.focus617.myopengldemo.util.ShaderHelper
import com.focus617.myopengldemo.util.TextResourceReader

/**
 * 着色器类 ShaderProgram
 * 1. 储存了着色器程序的ID [program]
 * 2. 它的构造器需要顶点着色器和片段着色器源代码的文件路径
 */
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
    fun use() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program)
    }

    //销毁着色器程序
    fun destroy(){
        glDeleteProgram(program)
    }

    /**
     * uniform工具函数
     * 所有的set…函数能够查询一个uniform的位置句柄，并设置它的值。
     */
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

    fun setVector3fv( attributeName:String, vector: Vector, count: Int)
    {
        setVector3fv( attributeName, floatArrayOf(vector.x, vector.y, vector.z), count)
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

    fun setTexture( attributeName:String, value: Int)
    {
        glUniform1i(glGetUniformLocation(program, attributeName), value )
    }
}
