package com.focus617.myopengldemo.data

import android.content.Context
import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.xuassimp.base.XuMesh
import com.focus617.myopengldemo.xuassimp.data.XuScene

class Model(val context: Context, path: String) {

    //模型所包含的网格
    private val meshes = mutableListOf<Mesh>()

    //模型文件所在目录
    private var directory: String =
        if (path.contains('/')) path.substring(0, path.lastIndexOf('/'))
        else "."

    init {
        loadModel(path)
    }


    //加载模型
    private fun loadModel(path: String) {
        val scene = XuScene(context)
        scene.loadFromObj(path)
        scene.dumpMesh()
        scene.dumpMaterials()

        // process ASSIMP's root node recursively
        processNode(scene.mRootNode, scene)
    }

    //销毁模型
    fun destroy() {}

    //渲染模型，即依次渲染各个网格
    fun draw(shaderProgram: ShaderProgram) {
        for (mesh in meshes)
            mesh.draw(shaderProgram);
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
    private fun processNode(node: Int, scene: XuScene){
        for(mesh in scene.mMeshes)
            meshes.add(processMesh(mesh, scene))
    }

    //生成网格
    private fun processMesh(mesh: XuMesh, scene: XuScene): Mesh{

        return Mesh.getRawMesh()
    }

    //创建纹理并加载图像数据
//    fun loadMaterialTextures(mat: aiMaterial, type: aiTextureType, typeName: String): vector<Texture>
}