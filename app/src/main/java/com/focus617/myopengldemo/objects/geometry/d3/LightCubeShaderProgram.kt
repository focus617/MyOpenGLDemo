package com.focus617.myopengldemo.objects.geometry.d3

import android.content.Context
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX

const val PATH = "Cube"
const val VERTEX_FILE = "light_vertex_shader.glsl"
const val FRAGMENT_FILE = "light_fragment_shader.glsl"

class LightCubeShaderProgram(context: Context) : ShaderProgram(
    context,
    PATH,
    VERTEX_FILE,
    FRAGMENT_FILE
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