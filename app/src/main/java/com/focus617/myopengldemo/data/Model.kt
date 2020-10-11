package com.focus617.myopengldemo.data

import com.focus617.myopengldemo.programs.ShaderProgram
import com.focus617.myopengldemo.programs.other.CubeShaderProgram

class Model(path: String) {

    init {
        loadModel(path);
    }
    //渲染模型，即依次渲染各个网格
    fun draw(shaderProgram: ShaderProgram){}

    //销毁模型的所有网格
    fun destroy(){}


    //模型所包含的网格
    private val meshes: List<Mesh> = mutableListOf()

    //模型文件所在目录
    private lateinit var directory: String

    //加载模型
    fun loadModel(path: String){}

    //处理 aiScene 对象包含的节点和子节点
//    fun processNode(node: aiNode, scene: aiScene){}

    //生成网格
//    fun processMesh(mesh: aiMesh, scene: aiScene): Mesh{}

    //创建纹理并加载图像数据
//    fun loadMaterialTextures(mat: aiMaterial, type: aiTextureType, typeName: String): vector<Texture>
}