package com.focus617.myopengldemo.programs.other

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_TEXTURE_UNIT
import com.focus617.myopengldemo.programs.ShaderConstants.U_VIEW_MATRIX

class LightCubeShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.light_vertex_shader,
    R.raw.light_fragment_shader
) {

    fun setUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ){
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)

    }
}