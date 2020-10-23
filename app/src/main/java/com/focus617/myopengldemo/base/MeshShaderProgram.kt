package com.focus617.myopengldemo.base

import android.content.Context
import android.opengl.GLES31
import com.focus617.myopengldemo.base.basic.PointLight
import com.focus617.myopengldemo.base.program.ShaderConstants
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.base.program.ShaderConstants.U_MODEL_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_PROJECT_MATRIX
import com.focus617.myopengldemo.base.program.ShaderConstants.U_VIEW_MATRIX
import com.focus617.myopengldemo.utils.Vector

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
        viewPosition: Vector,
        textureId: Int
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

        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureId)
        setTexture(ShaderConstants.U_TEXTURE_UNIT,0)// The 0 means "GL_TEXTURE0", or the first texture unit.
    }


//    private fun setTextures(shaderProgram: ShaderProgram) {
//
//        val PreFix = "material."
//
//        var diffuseNr = 1
//        var specularNr = 1
//
//        for ((index, texture) in textures.withIndex()) {
//            // 在绑定之前激活相应的纹理单元
//            glActiveTexture(GL_TEXTURE0 + index);
//
//            // 获取纹理序号（diffuse_textureN 中的 N）
//            var name: String
//            var number: String
//
//            when (textures[index].type) {
//                TextureType.TextureDiffuse -> {
//                    name = "texture_diffuse"
//                    number = (diffuseNr++).toString()
//                }
//                TextureType.TextureSpecular -> {
//                    name = "texture_specular"
//                    number = (specularNr++).toString()
//                }
//            }
//
//            /* 纹理命名标准：
//             * 每个漫反射纹理被命名为texture_diffuseN，
//             * 每个镜面光纹理应该被命名为texture_specularN，
//             * 其中N的范围是1到纹理采样器最大允许的数字。
//             */
//            shaderProgram.setInt((PreFix + name + number), index);
//
//            glBindTexture(GL_TEXTURE_2D, textures[index].id);
//        }
//        glActiveTexture(GL_TEXTURE0);
//    }
}