package com.focus617.myopengldemo.xuassimp.data

import android.content.Context
import android.text.TextUtils
import com.focus617.myopengldemo.xuassimp.base.Material
import com.focus617.myopengldemo.xuassimp.base.XuMesh
import com.focus617.myopengldemo.xuassimp.utils.MtlLoader
import com.focus617.myopengldemo.xuassimp.utils.ObjLoader
import timber.log.Timber

/**
 * 场景类
 * 对应于Assimp的 Scene类
 */
class XuScene(val context: Context) {

    var mMeshes = ArrayList<XuMesh>()

    var mMaterials = HashMap<String, Material>()      // 全部材质列表

    // 未实现的Mesh父子关系管理
    var mRootNode: Int = 0


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
        val mesh = ObjLoader.load(context, objFileName)
        mMeshes.add(mesh)

        if (mesh.mMaterial != null) {
            MtlLoader.load(context, mesh.mMaterial!!, mMaterials)
        }

    }

    fun dumpMesh() {
        Timber.d("dumpMesh()")
        for (mesh in mMeshes) {
            mesh.dump()
            mesh.dumpVertices()
            mesh.dumpNormals()
            mesh.dumpTextureCoords()
            mesh.dumpFaces()
        }
    }

    fun dumpMaterials() {
        Timber.d("dumpMaterials()")
        Timber.d("Size of Materials: ${mMaterials.size}")
        mMaterials.forEach { (key, material) ->
            Timber.d("\nMaterial[$key]:")
            material.dump()
        }
    }

}