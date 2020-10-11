package com.focus617.myopengldemo.util

import android.content.Context
import timber.log.Timber
import java.util.*

/**
 * 解析3D Obj 文件
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
    class ObjTexture(var s: Float, var t: Float, var w: Float)

    class ObjFaceElement(var vId: Int, var tId: Int, var nId: Int)
    class ObjFace(var x: ObjFaceElement, y: ObjFaceElement, z: ObjFaceElement)

    // 存放解析出来的顶点的坐标
    private val mVertexArrayList = ArrayList<ObjVertex>()

    //存放解析出来的法线坐标
    private val mNormalArrayList = ArrayList<ObjNormal>()

    //存放解析出来的纹理坐标
    private val mTextureArrayList = ArrayList<ObjTexture>()

    //存放解析出来面的索引
    private val mIndexArrayList = ArrayList<Short>()

    // 存放解析过程的中间数据
    private val verticesList = mutableListOf<String>()
    private val verticesNormalList = mutableListOf<String>()
    private val textureCoordsList = mutableListOf<String>()
    private val facesList = mutableListOf<String>()


    fun loadFromObjFile(context: Context, objFileName: String) {

        Timber.d("loadFromObjFile: $objFileName")

        try {
            val scanner = Scanner(context.assets.open(objFileName))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                if (line.startsWith("v")) {
                    verticesList.add(line)
                } else if (line.startsWith("vn")) {
                    verticesNormalList.add(line)
                } else if (line.startsWith("vt")) {
                    textureCoordsList.add(line)
                } else if (line.startsWith("f")) {
                    facesList.add(line)
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
            val x = coordinates[1].toFloat()
            val y = coordinates[2].toFloat()
            val z = coordinates[3].toFloat()
            mTextureArrayList.add(ObjTexture(x, y, z))
        }
    }

    /**
     * Uses [facesList] which is a list of lines from OBJ containing face data
     * to create element list
     */
    private fun analyzeFaces() {

        for (face in facesList) {
            Timber.d(face)

            val vertexIndices = face.split(" ").toTypedArray()

            if (!(vertexIndices[1].contains("/"))) {

                // vertexIndices[] format: vertexIndex
                for (i in 1 until vertexIndices.size) {
                    var vertexIndex = Integer.valueOf(vertexIndices[i])
                    mIndexArrayList.add(vertexIndex.toShort())
                }

            } else {

                // vertexIndices[] format: vertexIndex / textureIndex / normalIndex
                for (i in 1 until vertexIndices.size) {
                    val indices = vertexIndices[i].split("/").toTypedArray()
                    val vertexIndex = Integer.valueOf(indices[0])
                    val textureIndex = Integer.valueOf(indices[1])
                    val normalIndex = Integer.valueOf(indices[2])

                    // TODO: build Vertex Array here

                    mIndexArrayList.add(vertexIndex.toShort())
                }
            }

        }
    }

}
