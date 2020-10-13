package com.focus617.myopengldemo.xuassimp.base

/**
 * 场景类
 * 对应于Assimp的 Scene类
 */
class XuScene(){

    var mMeshes = ArrayList<XuMesh>()

    var mMaterials: HashMap<String, Material>? = null      // 全部材质列表

//    var mRootNode: Int
}