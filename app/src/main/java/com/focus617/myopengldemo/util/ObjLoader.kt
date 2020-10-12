package com.focus617.myopengldemo.util

import android.content.Context
import timber.log.Timber
import java.util.*

/**
 * 解析3D Wavefront Obj 文件
 * 解析obj文件时，数据类型由开头的首字母决定
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
class ObjLoader {

    class ObjVertex(var x: Float, var y: Float, var z: Float)

    class ObjNormal(var x: Float, var y: Float, var z: Float)

    class ObjTexture(var s: Float, var t: Float, var w: Float) {
        fun put(index: Int, value: Float) =
            when (index) {
                0 -> s = value
                1 -> t = value
                2 -> w = value
                else -> {
                }
            }
    }

    class ObjFaceElement(var vId: Int, var tId: Int, var nId: Int)
    class ObjFace(
        var fileName: String,
        var a: ObjFaceElement, var b: ObjFaceElement, var c: ObjFaceElement
    ) {
        fun put(index: Int, value: ObjFaceElement) =
            when (index) {
                0 -> a = value
                1 -> b = value
                2 -> c = value
                else -> {
                }
            }
    }

    // 存放解析出来的 mtl文件名称
    private var mMtlFileName: String = ""

    // 存放解析出来的顶点的坐标
    private val mVertexArrayList = ArrayList<ObjVertex>()

    //存放解析出来的法线坐标
    private val mNormalArrayList = ArrayList<ObjNormal>()
    private var hasNormal = false

    //存放解析出来的纹理坐标
    private val mTextureArrayList = ArrayList<ObjTexture>()
    private var hasTexture = false
    private var dimension = 2

    //存放解析出来面的索引
    private val mIndexArrayList = ArrayList<ObjFace>()

    // 存放解析过程的中间数据
    private val verticesList = mutableListOf<String>()
    private val verticesNormalList = mutableListOf<String>()
    private val textureCoordsList = mutableListOf<String>()
    private val facesList = mutableListOf<String>()

    fun load(context: Context, objFileName: String) {

        loadFromObjFile(context, objFileName)
        fillVertices()
        fillNormals()
        fillTextureCoords()
        analyzeFacesList()

    }

    private fun loadFromObjFile(context: Context, objFileName: String) {

        Timber.d("loadFromObjFile: $objFileName")

        try {
            val scanner = Scanner(context.assets.open(objFileName))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()

                when {
                    line.startsWith("mtlib") -> {
                        mMtlFileName = line.substring(line.indexOf("mtlib") + 7)
                    }
                    line.startsWith("v") -> {
                        verticesList.add(line)
                    }
                    line.startsWith("vn") -> {
                        verticesNormalList.add(line)
                    }
                    line.startsWith("vt") -> {
                        textureCoordsList.add(line)
                    }
                    line.startsWith("usemtl") -> {
                        facesList.add(line)
                    }
                    line.startsWith("f") -> {
                        facesList.add(line)
                    }
                }
            }
            scanner.close()
        } catch (ex: Exception) {
            Timber.e(ex.message.toString())
        }
    }

    /**
     * Uses [verticesList] which is a list of lines from OBJ containing vertex data
     * to create vertex array list
     */
    private fun fillVertices() {
        for (vertex in verticesList) {
            val coordinates = vertex.split(" ")
            val x = coordinates[1].toFloat()
            val y = coordinates[2].toFloat()
            val z = coordinates[3].toFloat()
            mVertexArrayList.add(ObjVertex(x, y, z))
        }
    }

    /**
     * Uses [verticesNormalList] which is a list of lines from OBJ containing vertex normal data
     * to create vertex normal array list
     */
    private fun fillNormals() {
        for (vertex in verticesNormalList) {
            val coordinates = vertex.split(" ")
            val x = coordinates[1].toFloat()
            val y = coordinates[2].toFloat()
            val z = coordinates[3].toFloat()
            mNormalArrayList.add(ObjNormal(x, y, z))
        }
    }

    /**
     * Uses [textureCoordsList] which is a list of lines from OBJ containing texture coords data
     * to create texture coords array list
     */
    private fun fillTextureCoords() {
        for (vertex in textureCoordsList) {
            val coordinates = vertex.split(" ")

            var objTexture = ObjTexture(0f, 0f, 0f)

            for (i in 1 until coordinates.size) {
                val coord = coordinates[i].toFloat()
                objTexture.put(i, coord)
            }
            mTextureArrayList.add(objTexture)
        }
    }

    /**
     * Uses [facesList] which is a list of lines from OBJ containing face data
     * to create element list
     */
    private fun analyzeFacesList() {
        Timber.d("analyzeFacesList()")

        for (face in facesList) {
            Timber.d(face)

            var textureFileName: String = ""
            val vertexIndices = face.split(" ").toTypedArray()

            if (vertexIndices[0].startsWith("usemtl")) {
                // 分析 usemtl Texture_n, 切换纹理贴图
                textureFileName = vertexIndices[1]
                Timber.d("Result: textureFileName = $textureFileName")

            } else {
                // 分析 f Index
                if (!(vertexIndices[1].contains("/"))) {
                    // vertexIndices[] format: "f vertexIndex1 vertexIndex2 vertexIndex3"
                    hasNormal = false
                    hasTexture = false

                    val defaultElement = ObjFaceElement(0, 0, 0)
                    val objFace =
                        ObjFace(textureFileName, defaultElement, defaultElement, defaultElement)

                    for (i in 1 until clamp(vertexIndices.size, 1, 3)) {
                        objFace.put(i, ObjFaceElement(Integer.valueOf(vertexIndices[i]), 0, 0))
                    }
                    mIndexArrayList.add(objFace)

                } else {
                    // vertexIndices[] format: "f vertexIndex/textureIndex/normalIndex .."
                    hasNormal = true
                    hasTexture = true

                    val defaultElement = ObjFaceElement(0, 0, 0)
                    val objFace =
                        ObjFace(textureFileName, defaultElement, defaultElement, defaultElement)

                    for (i in 1 until clamp(vertexIndices.size, 1, 3)) {
                        val indices = vertexIndices[i].split("/").toTypedArray()

                        val vertexIndex = Integer.valueOf(indices[0])
                        val textureIndex = Integer.valueOf(indices[1])
                        val normalIndex = Integer.valueOf(indices[2])

                        objFace.put(i, ObjFaceElement(vertexIndex, textureIndex, normalIndex))
                    }
                    mIndexArrayList.add(objFace)
                }

            }
        }

    }
}
