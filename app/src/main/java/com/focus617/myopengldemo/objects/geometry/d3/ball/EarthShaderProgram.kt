package com.focus617.myopengldemo.objects.geometry.d3.ball

import android.content.Context
import android.opengl.GLES31
import com.focus617.myopengldemo.base.basic.PointLight
import com.focus617.myopengldemo.base.program.ShaderConstants
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX
import com.focus617.myopengldemo.util.Vector

const val EARTH_PATH = "Earth"
const val EARTH_VERTEX_FILE = "vertex_shader.glsl"
const val EARTH_FRAGMENT_FILE = "earth_fragment_shader.glsl"

class EarthShaderProgram(context: Context) : ShaderProgram(
    context,
    EARTH_PATH,
    EARTH_VERTEX_FILE,
    EARTH_FRAGMENT_FILE
) {

    fun setUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        viewPosition: Vector,
        earthDayTextureId: Int,
        earthNightTextureId: Int
    ){
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)

        setVector3fv(ShaderConstants.U_POINT_VIEW_POSITION, viewPosition, 1)

        setVector3fv(ShaderConstants.U_POINT_LIGHT_POSITION, PointLight.position, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_AMBIENT, PointLight.ambient, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_DIFFUSE,  PointLight.diffuse, 1)
        setVector3fv(ShaderConstants.U_POINT_LIGHT_SPECULAR, PointLight.specular, 1)
        setFloat(ShaderConstants.U_POINT_LIGHT_CONSTANT, PointLight.Constant)
        setFloat(ShaderConstants.U_POINT_LIGHT_LINEAR, PointLight.Linear)
        setFloat(ShaderConstants.U_POINT_LIGHT_QUADRATIC, PointLight.Quadratic)

        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, earthDayTextureId)
        setTexture(ShaderConstants.U_TEXTURE_UNIT_1,0)// The 0 means "GL_TEXTURE0", or the first texture unit.

        GLES31.glActiveTexture(GLES31.GL_TEXTURE1)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, earthNightTextureId)
        setTexture(ShaderConstants.U_TEXTURE_UNIT_2,1)// The 1 means "GL_TEXTURE1", or the second texture unit.

    }
}