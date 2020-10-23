package com.focus617.myopengldemo.base

import android.content.Context
import com.focus617.myopengldemo.base.basic.Camera
import com.focus617.myopengldemo.utils.helper.TextureHelper
import com.focus617.myopengldemo.utils.objTools.*
import timber.log.Timber


//data class Texture(
//    var id: Int,
//    var fileName: String  // 我们储存纹理的路径用于与其它纹理进行比较
//)

data class TextureGroup(
    var TextureDiffuse: Int,
    var TextureSpecular: Int
)

class Model(val context: Context) {

    //模型所包含的Mesh集合
//    private val meshes = ArrayList<Mesh>()
    private val mMeshes = HashMap<String, Mesh>()

    //模型所包含的Material集合
    private val mMaterials = HashMap<String, TextureGroup>()

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

        loadMaterialTextures(objInfo.mMaterials)
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
    private fun loadMaterialTextures(mMaterialMap: HashMap<String, Material>) {
        if (mMaterialMap.size == 0) {
            val defaultPath = "$directory/$defaultTextureFile"
            val defaultTextureId =
                TextureHelper.loadTextureFromFile(context, defaultPath)

            textures_loaded[defaultPath] = defaultTextureId
            mMaterials[DEFAULT_GROUP_NAME] = TextureGroup(defaultTextureId,0)

        } else {
            for ((key, material) in mMaterialMap) {

                val textureDiffuse =
                    if (material.Kd_Texture != null) getTexture("$directory/${material.Kd_Texture}")
                    else 0

                val textureSpecular =
                    if (material.Ks_ColorTexture != null)
                        getTexture("$directory/${material.Ks_ColorTexture}")
                    else 0

                mMaterials[key] = TextureGroup(textureDiffuse, textureSpecular)
            }
        }

    }

    private fun getTexture(filePath: String): Int {
        return if (textures_loaded[filePath] == null) {
            val textureId = TextureHelper.loadTextureFromFile(context, filePath)
            textures_loaded[filePath] = textureId
            textureId
        } else textures_loaded[filePath]!!
    }


    fun initScene() {
//        val scene = XuScene(context)

        // process ASSIMP's root node recursively
//        processNode(scene.mRootNode, scene)
//        if(scene.mMesh != null)
//            processMesh(scene.mMesh!!)
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
                mMaterials[mesh.materialKey]!!.TextureDiffuse
            )
            mesh.draw()
        }
    }
}

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

