package com.focus617.myopengldemo.utils.objTools

import com.focus617.myopengldemo.base.objectbuilder.ObjectBuilder2.Companion.GeneratedData
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.iterator
import kotlin.collections.set

class ObjVertex(var x: Float, var y: Float, var z: Float) {
    override fun toString() = "($x, $y, $z)"
}

class ObjNormal(var x: Float, var y: Float, var z: Float) {
    override fun toString() = "($x, $y, $z)"
}

class ObjTexture(var s: Float, var t: Float, var w: Float) {
    fun put(index: Int, value: Float) =
        when (index) {
            0 -> s = value
            1 -> t = value
            2 -> w = value
            else -> {
            }
        }

    override fun toString() = "($s, $t, $w)"
}

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
class ObjInfo {
    // 解析对象名
    var name: String? = "def"

    // 存放解析出来的 mtl文件名称
    var mMtlFileName: String? = null

    // 存放解析出来的材质库
    var mMaterials = HashMap<String, Material>()

    // 存放解析出来的顶点的坐标
    val mVertices = ArrayList<ObjVertex>()

    //存放解析出来的法线坐标
    val mNormals = ArrayList<ObjNormal>()

    //存放解析出来的纹理坐标
    val mTextureCoords = ArrayList<ObjTexture>()

    //存放解析出来面的索引
    val mIndices = HashMap<String, ArrayList<Int>>()

    // 存放根据face信息解析的完整的顶点属性（包含法线和纹理）
    val mFinalVertices = HashMap<String, ArrayList<Float>>()

    var hasNormalInFace = false
    var hasTextureInFace = false
    var textureDimension = 2

    fun clear() {
        name = "def"
        mMtlFileName = null
        mMaterials.clear()
        mVertices.clear()
        mNormals.clear()
        mTextureCoords.clear()
        mIndices.clear()
        mFinalVertices.clear()
//        hasNormalInFace = false
//        hasTextureInFace = false
//        textureDimension = 2
    }

    fun translate(): HashMap<String, GeneratedData> {

        val meshList = HashMap<String, GeneratedData>()

        for ((key, indices) in mIndices) {
            Timber.d("parse(): parsing key: $key")
            val vertices = mFinalVertices[key] ?: continue

            var vertexSize = 3 + 3 + 3
//            if (hasNormalInFace) vertexSize += 3
//            if (hasTextureInFace) {
//                when (textureDimension) {
//                    2 -> vertexSize += 2
//                    3 -> vertexSize += 3
//                }
//            }
            val numVertices = vertices.size / vertexSize
            Timber.d("parse():vertexSize=$vertexSize, numVertices=$numVertices")

            val indexArray = ShortArray(indices.size)
            val vertexArray = FloatArray(vertices.size)

            Timber.d(
                "parse(): Size - V:${vertexArray.size}, I:${indexArray.size}"
            )
            // 将构造的顶点列表转存为顶点数组
            for (i in 0 until vertices.size) {
                vertexArray[i] = vertices[i]
            }

            // 将构造的顶点列表转存为顶点索引数组
            for (i in 0 until indices.size) {
                indexArray[i] = indices[i].toShort()
            }

            // 将构造的顶点列表转存为顶点数组
            meshList[key] =
                GeneratedData(numVertices, vertexArray, indexArray)
        }

        return meshList
    }

    fun dump() {
        Timber.d("ObjName: $name")
        Timber.d("MTLName: $mMtlFileName")
        Timber.d("Vertices Size: ${mVertices.size}")
        Timber.d("Normals Size: ${mNormals.size}")
        Timber.d("TextureCoords Size: ${mTextureCoords.size}")
        Timber.d("Faces Map Size: ${mIndices.size}")

//        Timber.d("Texture Dimension: $textureDimension")
    }

    fun dumpVertices() {
        Timber.d("mVertexArrayList dump:")
        val verticesSize = mVertices.size
        Timber.d("Vertex Size: $verticesSize")

        when (verticesSize) {
            0 -> return
            in 1..4 -> {
                for (i in 0..verticesSize)
                    Timber.d("Vertex[$i]: ${mNormals[i]}")
            }
            else -> {
                for (i in 0..2)
                    Timber.d("Vertex[$i]: ${mVertices[i]}")
                Timber.d("...")
                for (i in (verticesSize - 3) until verticesSize)
                    Timber.d("Vertex[$i]: ${mVertices[i]}")
            }
        }
    }

    fun dumpNormals() {
//        Timber.d("hasNormalInFace is $hasNormalInFace")

        Timber.d("mNormalList dump:")
        val normalArraySize = mNormals.size
        Timber.d("Normal Array Size: $normalArraySize")

        when (normalArraySize) {
            0 -> return
            in 1..4 -> {
                for (i in 0 until normalArraySize)
                    Timber.d("Normal[$i]: ${mNormals[i]}")
            }
            else -> {
                for (i in 0..2)
                    Timber.d("Normal[$i]: ${mNormals[i]}")
                Timber.d("...")
                for (i in (normalArraySize - 3) until normalArraySize)
                    Timber.d("Normal[$i]: ${mNormals[i]}")
            }
        }
    }

    fun dumpTextureCoords() {
//        Timber.d("hasTextureInFace is $hasTextureInFace")
//        Timber.d("textureDimension = $textureDimension")

        Timber.d("mTextureArrayList dump:")
        val textureArraySize = mTextureCoords.size
        Timber.d("Texture Array Size: $textureArraySize")

        when (textureArraySize) {
            0 -> return
            in 1..4 -> {
                for (i in 0..textureArraySize)
                    Timber.d("Texture[$i]: ${mTextureCoords[i]}")
            }
            else -> {
                for (i in 0..2) Timber.d("Texture[$i]: ${mTextureCoords[i]}")
                Timber.d("...")
                for (i in (textureArraySize - 3) until textureArraySize)
                    Timber.d("Texture[$i]: ${mTextureCoords[i]}")
            }
        }
    }

    fun dumpFaces() {
        Timber.d("mFaceList dump:")
        Timber.d("Face Map Size: ${mIndices.size}")

        for ((key, faceList) in mIndices) {

            Timber.d("FaceList for '$key': size=${faceList.size}")

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


