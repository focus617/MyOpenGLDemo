package com.focus617.myopengldemo.programs.other

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderConstants.LIGHT_COLOR
import com.focus617.myopengldemo.programs.ShaderConstants.OBJECT_COLOR
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_TEXTURE_UNIT
import com.focus617.myopengldemo.programs.ShaderConstants.U_VIEW_MATRIX

class CubeShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.cube_vertex_shader,
    R.raw.cube_fragment_shader
) {
    private val LightColor: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f)
    private val ObjectColor: FloatArray = floatArrayOf(1.0f, 0.5f, 0.31f)

    fun setUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ){
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)

        setVector3fv(OBJECT_COLOR, ObjectColor, 1)
        setVector3fv(LIGHT_COLOR, LightColor, 1)

    }
}