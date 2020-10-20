package com.focus617.myopengldemo.objects.geometry.d3

import android.content.Context
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_R
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX

const val BALL_PATH = "Ball"
const val BALL_VERTEX_FILE = "vertex_shader.glsl"
const val BALL_FRAGMENT_FILE = "fragment_shader.glsl"

class BallShaderProgram(context: Context) : ShaderProgram(
    context,
    BALL_PATH,
    BALL_VERTEX_FILE,
    BALL_FRAGMENT_FILE
) {

    fun setUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        uR: Float
    ){
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)
        setFloat(U_R, uR)
    }
}