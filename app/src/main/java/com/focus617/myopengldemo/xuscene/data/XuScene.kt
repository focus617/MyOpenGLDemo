package com.focus617.myopengldemo.xuscene.data

import android.content.Context
import android.text.TextUtils
import com.focus617.myopengldemo.xuscene.base.XuMesh
import com.focus617.myopengldemo.xuscene.utils.ObjLoader
import timber.log.Timber

/**
 * 场景类
 * 对应于Assimp的 Scene类
 */
class XuScene(val context: Context) {

    var mMesh: XuMesh? = null

    // 材质库：统一放入XuMesh
//    var mMaterials = HashMap<String, Material>()

    // 未实现的Mesh父子关系管理
//    var mRootNode: Int = 0


    /**
     * 加载并分析Obj文件，构造 Meshes 和 Materials
     * @param context   Context
     * @param objFileName assets的obj文件路径
     * @return
     */
    fun loadFromObj(objFileName: String) {

        if (objFileName.isEmpty() or TextUtils.isEmpty(objFileName)) {
            Timber.w("Obj File doesn't exist")
            return
        }
        mMesh = ObjLoader.load(context, objFileName)
    }

    fun dumpMesh() {
        Timber.d("dumpMesh()")
        mMesh?.dump()
        mMesh?.dumpVertices()
        mMesh?.dumpNormals()
        mMesh?.dumpTextureCoords()
        mMesh?.dumpFaces()
        mMesh?.dumpMaterials()
    }

}