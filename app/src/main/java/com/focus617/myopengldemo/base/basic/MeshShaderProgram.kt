package com.focus617.myopengldemo.base.basic

import android.content.Context
import android.opengl.GLES31
import com.focus617.myopengldemo.base.program.ShaderConstants
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX
import com.focus617.myopengldemo.utils.Vector

const val PATH = "3dModel/teapot"
const val VERTEX_FILE = "vertex_shader.glsl"
const val FRAGMENT_FILE = "fragment_shader.glsl"

class MeshShaderProgram(context: Context) : ShaderProgram(
    context,
    PATH,
    VERTEX_FILE,
    FRAGMENT_FILE
) {

    fun setUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,

        viewPosition: Vector,

        specular: FloatArray,
        shininess: Float,
        textureDiffuseId: Int
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

        setVector3fv(ShaderConstants.U_MATERIAL_SPECULAR, specular,1)
        setFloat(ShaderConstants.U_MATERIAL_SHININESS, shininess)

        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureDiffuseId)
        setTexture(ShaderConstants.U_MATERIAL_TEXTURE_DIFFUSE,0)
    }



}