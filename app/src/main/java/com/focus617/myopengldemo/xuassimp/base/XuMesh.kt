package com.focus617.myopengldemo.xuassimp.base

import com.focus617.myopengldemo.xuassimp.utils.ObjLoader
import timber.log.Timber
import java.util.ArrayList


/**
 * obj文件信息类
 * 对应于Assimp的 Mesh类
 *
 *  解析obj文件时，数据类型由开头的首字母决定
 *   switch(首字母){
 *       case "v":  顶点坐标数据
 *          break;
 *       case "vn": 法向量坐标数据
 *          break;
 *       case "vt": 纹理坐标数据
 *          break;
 *       case "usemtl": 材质
 *          break;
 *       case "f": 面索引
 *          break;
 *   }
 */
class XuMesh {
    // 解析对象名
    var name: String? = "def"

    // 存放解析出来的 mtl文件名称
    var mMtlFileName: String? = null

    // 存放解析出来的材质库
    var mMaterials = HashMap<String, Material>()

    // 存放解析出来的顶点的坐标
    val mVertices = ArrayList<ObjLoader.ObjVertex>()

    //存放解析出来的法线坐标
    val mNormals = ArrayList<ObjLoader.ObjNormal>()

    //存放解析出来的纹理坐标
    val mTextureCoords = ArrayList<ObjLoader.ObjTexture>()

    //存放解析出来面的索引
    val mFaces = HashMap<String, ArrayList<Face>>()

    var hasNormalInFace = false
    var hasTextureInFace = false
    var textureDimension = 2

    fun clear(){
        name = "def"
        mMtlFileName = null
        mVertices.clear()
        mNormals.clear()
        mTextureCoords.clear()
        mFaces.clear()
        hasNormalInFace = false
        hasTextureInFace = false
        textureDimension = 2
    }

    fun dump() {
        Timber.d("ObjName: $name")
        Timber.d("MTLName: $mMtlFileName")
        Timber.d("Vertices Size: ${mVertices.size}")
        Timber.d("Normals Size: ${mNormals.size}")
        Timber.d("TextureCoords Size: ${mTextureCoords.size}")
        Timber.d("Texture Dimension: $textureDimension")
        Timber.d("Faces Map Size: ${mFaces.size}")
    }

    fun dumpVertices() {
        Timber.d("mVertexArrayList dump:")
        val verticesSize = mVertices.size
        Timber.d("Vertex Size: $verticesSize")

        if (verticesSize < 4) {
            for (i in 0..verticesSize)
                Timber.d("Vertex[$i]: ${mNormals[i]}")
        } else {
            for (i in 0..2)
                Timber.d("Vertex[$i]: ${mVertices[i]}")
            Timber.d("...")
            for (i in (verticesSize - 3) until verticesSize)
                Timber.d("Vertex[$i]: ${mVertices[i]}")
        }
    }

    fun dumpNormals() {
        Timber.d("mNormalList dump:")
        val normalArraySize = mNormals.size
        Timber.d("Normal Array Size: $normalArraySize")

        if (normalArraySize < 4) {
            for (i in 0..normalArraySize)
                Timber.d("Normal[$i]: ${mNormals[i]}")
        } else {
            for (i in 0..2)
                Timber.d("Normal[$i]: ${mNormals[i]}")
            Timber.d("...")
            for (i in (normalArraySize - 3) until normalArraySize)
                Timber.d("Normal[$i]: ${mNormals[i]}")
        }
    }

    fun dumpTextureCoords() {
        Timber.d("mTextureArrayList dump:")
        val textureArraySize = mTextureCoords.size
        Timber.d("Texture Array Size: $textureArraySize")

        if (textureArraySize < 4) {
            for (i in 0..textureArraySize)
                Timber.d("Texture[$i]: ${mTextureCoords[i]}")
        } else {
            for (i in 0..2)
                Timber.d("Texture[$i]: ${mTextureCoords[i]}")
            Timber.d("...")
            for (i in (textureArraySize - 3) until textureArraySize)
                Timber.d("Texture[$i]: ${mTextureCoords[i]}")
        }
    }

    fun dumpFaces() {
        Timber.d("mFaceList dump:")
        Timber.d("Face Map Size: ${mFaces.size}")

        for ((key, faceList) in mFaces) {

            Timber.d("FaceList for $key: size=${faceList.size}")

            if (faceList.size < 4) {
                for (i in 0..faceList.size)
                    Timber.d("Face[$i]: ${faceList[i]}")
            } else {
                for (i in 0..2)
                    Timber.d("Face[$i]: ${faceList[i]}")
                Timber.d("...")
                for (i in (faceList.size - 3) until faceList.size)
                    Timber.d("Face[$i]: ${faceList[i]}")
            }
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


