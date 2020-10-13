package com.focus617.myopengldemo.xuassimp.data

import android.content.Context
import android.text.TextUtils
import com.focus617.myopengldemo.xuassimp.base.Material
import com.focus617.myopengldemo.xuassimp.base.XuMesh
import com.focus617.myopengldemo.xuassimp.utils.ObjLoader
import timber.log.Timber

/**
 * 场景类
 * 对应于Assimp的 Scene类
 */
object XuScene {

    var mMeshes = ArrayList<XuMesh>()

    var mMaterials = HashMap<String, Material>()      // 全部材质列表

//    var mRootNode: Int


    /**
     * 加载并分析Obj文件，构造 current ObjInfo
     * @param context   Context
     * @param objFileName assets的obj文件路径
     * @return
     */
    fun loadFromObj(context: Context, objFileName: String) {

        if (objFileName.isEmpty() or TextUtils.isEmpty(objFileName)) {
            Timber.w("Obj File doesn't exist")
            return
        }
        val mesh = ObjLoader.load(context, objFileName)
        mMeshes.add(mesh)
    }

    fun dumpMesh() {
        Timber.d("\ndumpMesh()")
        for (mesh in mMeshes) {
            mesh.dump()
            mesh.dumpVertices()
            mesh.dumpNormals()
            mesh.dumpTextureCoords()
            mesh.dumpFaces()
        }
    }

    fun dumpMaterials(){
        Timber.d("\ndumpMaterials()")
        Timber.d("Size of Materials: ${mMaterials.size}")
        mMaterials.forEach { (key, material) ->
            Timber.d("\nMaterial[$key]:")
            material.dump()
        }
    }

}