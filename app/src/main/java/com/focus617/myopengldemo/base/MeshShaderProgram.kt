package com.focus617.myopengldemo.base

import android.content.Context
import com.focus617.myopengldemo.base.basic.PointLight
import com.focus617.myopengldemo.base.program.ShaderConstants
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX
import com.focus617.myopengldemo.util.Vector

const val LIGHT_PATH = "3dModel/teapot"
const val LIGHT_VERTEX_FILE = "vertex_shader.glsl"
const val LIGHT_FRAGMENT_FILE = "fragment_shader.glsl"

class MeshShaderProgram(context: Context) : ShaderProgram(
    context,
    LIGHT_PATH,
    LIGHT_VERTEX_FILE,
    LIGHT_FRAGMENT_FILE
) {

    fun setUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        viewPosition: Vector
    ) {
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)

        setVector3fv(ShaderConstants.U_POINT_VIEW_POSITION, viewPosition, 1)

        setVector3fv(ShaderConstants.U_POINT_LIGHT_POSITION, PointLight.position, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_AMBIENT, PointLight.ambient, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_DIFFUSE, PointLight.diffuse, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_SPECULAR, PointLight.specular, 1)
        setFloat(ShaderConstants.U_POINT_LIGHT_CONSTANT, PointLight.Constant)
        setFloat(ShaderConstants.U_POINT_LIGHT_LINEAR, PointLight.Linear)
        setFloat(ShaderConstants.U_POINT_LIGHT_QUADRATIC, PointLight.Quadratic)
    }
}