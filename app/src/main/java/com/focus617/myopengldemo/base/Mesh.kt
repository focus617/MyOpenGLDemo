package com.focus617.myopengldemo.base

import android.content.Context
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.base.objectbuilder.IndexMeshObject
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder2
import com.focus617.myopengldemo.base.program.ShaderProgram
import com.focus617.myopengldemo.objects.geometry.d3.cube.Cube
import com.focus617.myopengldemo.objects.geometry.d3.cube.LightCubeShaderProgram
import com.focus617.myopengldemo.xuscene.utils.ObjInfo
import timber.log.Timber
import java.nio.*
import kotlin.properties.Delegates

enum class TextureType {
    TextureDiffuse,        // 漫反射纹理
    TextureSpecular        // 镜面光纹理
}

data class Texture(
    var id: Int,
    var type: TextureType,
    var fileName: String
)

/**
 * 本对象负责将顶点属性和索引加载到 GPU，并执行显示操作
 */
class Mesh(
    context: Context,
    val data: ObjectBuilder2.Companion.GeneratedData
): IndexMeshObject(context) {

    init {
        //调用初始化顶点数据的initVertexArray方法
        initVertexArray()

        //调用初始化着色器的intShader方法
        initShader()
    }

    override fun initVertexArray() {
        //顶点坐标数据的初始化
        build(data)
    }

    override fun initShader() {
        //自定义渲染管线程序
        mProgram = LightCubeShaderProgram(context)
        bindData()
    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {
        mProgram.use()
        (mProgram as LightCubeShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix
        )
    }

    override fun bindData() {
        val attribPropertyList: List<AttributeProperty> = arrayListOf(
            // 顶点的位置属性
            AttributeProperty(
                Cube.VERTEX_POS_INDEX,
                Cube.VERTEX_POS_SIZE,
                Cube.VERTEX_STRIDE,
                Cube.VERTEX_POS_OFFSET
            ),

            // 顶点的法线
            AttributeProperty(
                Cube.VERTEX_NORMAL_INDEX,
                Cube.VERTEX_NORMAL_SIZE,
                Cube.VERTEX_STRIDE,
                Cube.VERTEX_NORMAL_OFFSET
            ),

            // 顶点的纹理坐标
            AttributeProperty(
                Cube.VERTEX_TEXCOORDO_INDEX,
                Cube.VERTEX_TEXCOORDO_SIZE,
                Cube.VERTEX_STRIDE,
                Cube.VERTEX_TEX_COORDO_OFFSET
            )
        )
        super.bindData(attribPropertyList)
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

//    fun draw(shaderProgram: ShaderProgram) {
//
//        shaderProgram.use()
//
//        // TODO: pass parameter to GPU
//        // shaderProgram.setUniforms()
//
////        setTextures(shaderProgram)
//
//        // Bind the VAO and then draw with VAO settings
//        glBindVertexArray(mVaoId)
//
//        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)
//
//        // Reset to the default VAO
//        glBindVertexArray(0)
//
//        glBindBuffer(GL_ARRAY_BUFFER, 0)
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
//    }

    // 销毁纹理和缓冲区对象
//    fun destroy(){
//        for (texture in textures) {
//            glDeleteTextures(1, texture.id);
//        }
//        glDeleteBuffers(1, mElementId)
//        glDeleteBuffers(1, numVertices)
//        glDeleteVertexArrays(1,mVaoId)
//    }

    companion object {

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_NORMAL_INDEX = 1
        internal const val VERTEX_TEXCOORDO_INDEX = 2

        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_SIZE = 3            //x,y,z
        internal const val VERTEX_NORMAL_SIZE = 3         //NX, NY, NZ
        internal const val VERTEX_TEXCOORDO_SIZE = 2      //s and t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_NORMAL_OFFSET = VERTEX_POS_SIZE * Float.SIZE_BYTES
        internal const val VERTEX_TEX_COORDO_OFFSET =
            (VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE) * Float.SIZE_BYTES

        internal const val VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORDO_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }


}


