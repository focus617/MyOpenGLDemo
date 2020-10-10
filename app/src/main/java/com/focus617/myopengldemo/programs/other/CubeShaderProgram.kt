package com.focus617.myopengldemo.programs.other

import android.content.Context
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.programs.ShaderConstants.U_IT_MV_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_MATERIAL_COLOR
import com.focus617.myopengldemo.programs.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_POINT_LIGHT_COLOR
import com.focus617.myopengldemo.programs.ShaderConstants.U_POINT_LIGHT_POSITION
import com.focus617.myopengldemo.programs.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.programs.ShaderConstants.U_VIEW_MATRIX
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.util.Geometry.Companion.Vector

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
        projectionMatrix: FloatArray,
        it_mvMatrix: FloatArray,

        pointLightPosition: Vector,
        pointLightColor: Vector,
        materialColor: Vector
    ){
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)
        setMatrix4fv(U_IT_MV_MATRIX, it_mvMatrix)

        setVector3fv(U_POINT_LIGHT_POSITION, pointLightPosition, 1)
        setVector3fv(U_POINT_LIGHT_COLOR, pointLightColor, 1)
        setVector3fv(U_MATERIAL_COLOR, materialColor, 1)
    }
}

