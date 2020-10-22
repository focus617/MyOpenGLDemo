package com.focus617.myopengldemo.base

import android.content.Context
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.base.objectbuilder.IndexMeshObject
import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder2


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
        mVertexArray.dump()
        mElementArray.dump()
    }

    override fun initShader() {
        //自定义渲染管线程序
        mProgram = MeshShaderProgram(context)
        bindData()
    }

    fun updateShaderUniforms(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {
        mProgram.use()
        (mProgram as MeshShaderProgram).setUniforms(
            modelMatrix,
            viewMatrix,
            projectionMatrix
        )
    }

    override fun bindData() {
        val attribPropertyList: List<AttributeProperty> = arrayListOf(
            // 顶点的位置属性
            AttributeProperty(
                VERTEX_POS_INDEX,
                VERTEX_POS_SIZE,
                VERTEX_STRIDE,
                VERTEX_POS_OFFSET
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

        internal const val VERTEX_ATTRIBUTE_SIZE =  VERTEX_POS_SIZE

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES

    }


}


