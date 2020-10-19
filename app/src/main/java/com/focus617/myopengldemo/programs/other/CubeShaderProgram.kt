package com.focus617.myopengldemo.programs.other

import android.content.Context
import android.opengl.GLES31
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.base.program.ShaderConstants.U_IT_MV_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MATERIAL_DIFFUSE
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MATERIAL_SHININESS
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MATERIAL_SPECULAR
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_LIGHT_AMBIENT
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_LIGHT_DIFFUSE
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_LIGHT_POSITION
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_LIGHT_SPECULAR
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_LIGHT_CONSTANT
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_LIGHT_LINEAR
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_LIGHT_QUADRATIC
import com.focus617.myopengldemo.base.program.ShaderConstants.U_POINT_VIEW_POSITION
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import com.focus617.myopengldemo.base.basic.PointLight
import com.focus617.myopengldemo.base.basic.Material


class CubeShaderProgram(context: Context) : ShaderProgram(
    context,
    R.raw.cube_vertex_shader,
    R.raw.cube_fragment_shader
) {
    fun setUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        it_mvMatrix: FloatArray,

        viewPosition: Vector,
        boxTextureId: Int
    ){
        setMatrix4fv(U_MODEL_MATRIX, modelMatrix)
        setMatrix4fv(U_VIEW_MATRIX, viewMatrix)
        setMatrix4fv(U_PROJECT_MATRIX, projectionMatrix)
        setMatrix4fv(U_IT_MV_MATRIX, it_mvMatrix)

        setVector3fv(U_POINT_VIEW_POSITION, viewPosition, 1)

        setVector3fv(U_MATERIAL_SPECULAR, Material.specular,1)
        setFloat(U_MATERIAL_SHININESS, Material.shininess)

        setVector3fv(U_POINT_LIGHT_POSITION, PointLight.position, 1)
        setVector3fv(U_POINT_LIGHT_AMBIENT, PointLight.ambient, 1)
        setVector3fv(U_POINT_LIGHT_DIFFUSE,  PointLight.diffuse, 1)
        setVector3fv(U_POINT_LIGHT_SPECULAR, PointLight.specular, 1)
        setFloat(U_POINT_LIGHT_CONSTANT, PointLight.Constant)
        setFloat(U_POINT_LIGHT_LINEAR, PointLight.Linear)
        setFloat(U_POINT_LIGHT_QUADRATIC, PointLight.Quadratic)

        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, boxTextureId)
        setTexture(U_MATERIAL_DIFFUSE,0)
    }
}

