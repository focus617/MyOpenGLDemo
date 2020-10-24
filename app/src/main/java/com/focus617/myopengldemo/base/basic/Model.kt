package com.focus617.myopengldemo.base.basic

import android.content.Context
import com.focus617.myopengldemo.utils.helper.TextureHelper
import com.focus617.myopengldemo.utils.objTools.*
import timber.log.Timber


class Model(val context: Context) {

    //模型所包含的Mesh集合
//    private val meshes = ArrayList<Mesh>()
    private val mMeshes = HashMap<String, Mesh>()

    //模型所包含的Material集合
    private val mMaterials = HashMap<String, Material>()

    // 对所有加载过的纹理全局储存，防止重复加载
    private val textures_loaded = HashMap<String, Int>()

    //销毁模型及其所有Mesh
    fun destroy() {
        mMeshes.clear()
        mMaterials.clear()
    }

    //模型文件所在目录
    private lateinit var directory: String

    //加载模型
    fun loadFromObj(pathName: String) {
        directory = if (pathName.contains('/')) pathName.substring(0, pathName.lastIndexOf('/'))
        else "."
        Timber.d("load(): directory = $directory")

        val objInfo = ObjLoader.load(context, pathName)

        loadMaterialTextures(objInfo.mMaterialInfos)
        processMesh(objInfo)
    }

    //生成网格
    private fun processMesh(objInfo: ObjInfo) {

        val dataList = objInfo.translate()
        objInfo.clear()

        for ((key, data) in dataList) {
            mMeshes[key] = Mesh(context, key, data)
        }
    }

    //创建纹理并加载图像数据
    private fun loadMaterialTextures(mMaterialMap: HashMap<String, MaterialInfo>) {
        if (mMaterialMap.size == 0) {
            val defaultPath = "$directory/$defaultTextureFile"
            val defaultTextureId =
                TextureHelper.loadTextureFromFile(context, defaultPath)

            //将构建的textureId存进已构建纹理库
            textures_loaded[defaultPath] = defaultTextureId
            // 构建缺省材料
            val defaultMaterial = Material(DEFAULT_GROUP_NAME)
            defaultMaterial.textureDiffuse = defaultTextureId
            mMaterials[DEFAULT_GROUP_NAME] = defaultMaterial

        } else {
            for ((materialName, materialInfo) in mMaterialMap) {

                val textureDiffuse =
                    if (materialInfo.Kd_Texture != null) getTexture("$directory/${materialInfo.Kd_Texture}")
                    else 0

                val textureSpecular =
                    if (materialInfo.Ks_ColorTexture != null)
                        getTexture("$directory/${materialInfo.Ks_ColorTexture}")
                    else 0

                // 构建材料
                val material = Material(materialName)
                material.textureDiffuse = textureDiffuse
                material.textureSpecular = textureSpecular
                mMaterials[materialName] = material
            }
        }

    }

    private fun getTexture(filePath: String): Int {
        //如果纹理库中还没有需要构建的纹理，创建新的纹理
        return if (textures_loaded[filePath] == null) {
            val textureId = TextureHelper.loadTextureFromFile(context, filePath)
            //将构建的textureId存进已构建纹理库
            textures_loaded[filePath] = textureId
            textureId
        }
        //如果纹理库中已有构建好的纹理，直接使用
        else textures_loaded[filePath]!!
    }

    //渲染模型，即依次渲染各个网格
    fun draw(
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ) {
        for ((key, mesh) in mMeshes) {
            mesh.positionObjectInScene()
            mesh.updateShaderUniforms(
                mesh.mModelMatrix, viewMatrix, projectionMatrix,
                Camera.Position,
                mMaterials[mesh.materialName]!!.specular.toFloatArray(),
                mMaterials[mesh.materialName]!!.shininess,
                mMaterials[mesh.materialName]!!.textureDiffuse
            )
            mesh.draw()
        }
    }


}

//fun initScene() {
//        val scene = XuScene(context)
//
//        //process ASSIMP's root node recursively
//        processNode(scene.mRootNode, scene)
//        if(scene.mMesh != null)
//            processMesh(scene.mMesh!!)
//}
/**
 * 处理 Scene 对象包含的节点和子节点
 *
 * 我们也可以不处理任何的节点，只需要遍历场景对象的所有网格，这样就不需要为了索引做这一堆复杂的东西了。
 * 我们仍这么做的原因是，使用节点的最初想法是将网格之间定义一个父子关系。通过这样递归地遍历这层关系，
 * 我们就能将某个网格定义为另一个网格的父网格了。
 * 这个系统的一个使用案例是，当你想位移一个汽车的网格时，你可以保证它的所有子网格（比如引擎网格、方向盘网格、
 * 轮胎网格）都会随着一起位移。这样的系统能够用父子关系很容易地创建出来。
 */
//    fun processNode(node: Node, scene: XuScene){}

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

