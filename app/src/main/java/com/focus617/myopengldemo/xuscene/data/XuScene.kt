package com.focus617.myopengldemo.xuscene.data

import android.content.Context
import com.focus617.myopengldemo.utils.objTools.ObjInfo

/**
 * 场景类
 * 对应于Assimp的 Scene类
 */
class XuScene(val context: Context) {

    var mMesh: ObjInfo? = null

    // 材质库：统一放入XuMesh
//    var mMaterials = HashMap<String, Material>()

    // 未实现的Mesh父子关系管理
//    var mRootNode: Int = 0




}