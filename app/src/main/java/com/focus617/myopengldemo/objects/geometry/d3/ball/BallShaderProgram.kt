package com.focus617.myopengldemo.objects.geometry.d3.ball

import android.content.Context
import com.focus617.myopengldemo.base.basic.PointLight
import com.focus617.myopengldemo.base.program.ShaderConstants
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_R
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX
import com.focus617.myopengldemo.utils.Vector

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

        viewPosition: Vector,
        uR: Float
    ){
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)

        setFloat(U_R, uR)

        setVector3fv(ShaderConstants.U_POINT_VIEW_POSITION, viewPosition, 1)

        setVector3fv(ShaderConstants.U_POINT_LIGHT_POSITION, PointLight.position, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_AMBIENT, PointLight.ambient, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_DIFFUSE,  PointLight.diffuse, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_SPECULAR, PointLight.specular, 1)
        setFloat(ShaderConstants.U_POINT_LIGHT_CONSTANT, PointLight.Constant)
        setFloat(ShaderConstants.U_POINT_LIGHT_LINEAR, PointLight.Linear)
        setFloat(ShaderConstants.U_POINT_LIGHT_QUADRATIC, PointLight.Quadratic)
    }
}