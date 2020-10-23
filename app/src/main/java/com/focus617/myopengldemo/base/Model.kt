package com.focus617.myopengldemo.base

import android.content.Context
import com.focus617.myopengldemo.base.basic.Camera
import com.focus617.myopengldemo.utils.objTools.ObjLoader
import timber.log.Timber

class Model() {

    //模型所包含的Mesh集合
//    private val meshes = mutableListOf<IndexMeshObject>()
    val meshes = HashMap<String, Mesh>()

    //模型文件所在目录
    private lateinit var directory: String

    //加载模型
    fun load(context: Context, pathName: String) {
        directory = if (pathName.contains('/')) pathName.substring(0, pathName.lastIndexOf('/'))
        else "."
        Timber.d("load(): directory = $directory")

        val dataList = ObjLoader.loadFromObjFile(context, pathName)

        for ((key, data) in dataList) {
            meshes[key] = Mesh(context, data)
        }
    }


    fun initScene() {
//        val scene = XuScene(context)

        // process ASSIMP's root node recursively
//        processNode(scene.mRootNode, scene)
//        if(scene.mMesh != null)
//            processMesh(scene.mMesh!!)
    }

    //销毁模型及其所有Mesh
    fun destroy() {}

    //渲染模型，即依次渲染各个网格
    fun draw(
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ) {
        for ((key, mesh) in meshes) {
            mesh.positionObjectInScene()
            mesh.updateShaderUniforms(
                mesh.mModelMatrix, viewMatrix, projectionMatrix,
                Camera.Position)
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

//生成网格
//    private fun processMesh(mesh: ObjInfo): Mesh = Mesh.build(mesh)

//创建纹理并加载图像数据
//    fun loadMaterialTextures(mat: aiMaterial, type: aiTextureType, typeName: String): vector<Texture>
