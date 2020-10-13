package com.focus617.myopengldemo.xuassimp.utils

import android.content.Context
import android.text.TextUtils
import com.focus617.myopengldemo.xuassimp.base.Face
import com.focus617.myopengldemo.xuassimp.base.FaceElement
import com.focus617.myopengldemo.xuassimp.base.XuMesh
import com.focus617.myopengldemo.util.clamp
import timber.log.Timber
import java.util.*


/**
 * @description Wavefront Obj 3D模型文件解析类
 */
object ObjLoader {

    private lateinit var currentMesh : XuMesh
    private var currentTextureName: String = ""     // 存放解析出来的face当前使用的texture
    private var mMtlFiles = ArrayList<String>()     // 存放解析出来的 mtl文件名称

    fun load(context: Context, objFileName: String): XuMesh {

        Timber.d("loadFromObjFile(): $objFileName")
        currentMesh = XuMesh()
        currentMesh.name = objFileName

        try {
            val scanner = Scanner(context.assets.open(objFileName))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()

                when {
                    line.isEmpty() -> continue

                    line.startsWith(ANNOTATION) -> continue  // 注释行

                    line.startsWith(MTLLIB) -> {
                        fillMtlLib(line)
                    }
                    line.startsWith(O) -> {
                        fillObjName(line)
                    }
                    line.startsWith(VN) -> {
                        fillNormalList(line)
                    }
                    line.startsWith(VT) -> {
                        fillTextureCoordList(line)
                    }
                    line.startsWith(V) -> {
                        fillVertexList(line)
                    }
                    line.startsWith(USEMTL) -> {
                        switchTexture(line)
                    }
                    line.startsWith(F) -> {
                        fillFaceList(line)
                    }
                    else -> {
                        Timber.d("dropped: $line")
                    }
                }
            }
            scanner.close()
        } catch (ex: Exception) {
            Timber.e(ex.message.toString())
        }

        // TODO: How to move Scene's MtlMap out of MtlLoader
        for(file in mMtlFiles) {
            MtlLoader.load(context, file)
        }

        return currentMesh
    }

    // 对象名称
    private fun fillObjName(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return
        currentMesh.name = items[1]
    }

    // 材质
    private fun fillMtlLib(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return
        if (!TextUtils.isEmpty(items[1])) {
            mMtlFiles.add(items[1])
        }
    }

    /**
     * build [XuMesh.mVertices] based on line from OBJ containing vertex data
     */
    private fun fillVertexList(line: String) {
        val coordinates = line.split(DELIMITER)
        if (coordinates.size != 4) return

        val x = coordinates[1].toFloat()
        val y = coordinates[2].toFloat()
        val z = coordinates[3].toFloat()
        currentMesh.mVertices.add(ObjVertex(x, y, z))
    }

    /**
     * build [XuMesh.mNormals] based on line from OBJ containing vertex data
     */
    private fun fillNormalList(line: String) {
        val vectors = line.split(DELIMITER)
        if (vectors.size != 4) return

        val x = vectors[1].toFloat()
        val y = vectors[2].toFloat()
        val z = vectors[3].toFloat()
        currentMesh.mNormals.add(ObjNormal(x, y, z))
    }

    /**
     * build [XuMesh.mTextureCoords] based on line from OBJ containing vertex data
     *
     * 这里纹理的Y值，需要(Y = 1-Y0),原因是openGl的纹理坐标系与android的坐标系存在Y值镜像的状态
     */
    private fun fillTextureCoordList(line: String) {
        val coordinates = line.split(DELIMITER)
        when (coordinates.size) {
            3 -> currentMesh.textureDimension = 2
            4 -> currentMesh.textureDimension = 3
            !in 3..4 -> return
        }

        val objTexture = ObjTexture(0f, 0f, 0f)
        objTexture.put(0, coordinates[1].toFloat())

        // 纹理的Y值，需要(Y = 1-Y0)
        objTexture.put(1, 1f - coordinates[2].toFloat())

        if (coordinates.size == 4) {
            objTexture.put(2, coordinates[3].toFloat())
        }
        currentMesh.mTextureCoords.add(objTexture)
    }

    private fun switchTexture(line: String) {
        val textureName = line.split(DELIMITER)
        when (textureName.size) {
            2 -> currentTextureName = textureName[1]
            else -> return
        }
    }

    /**
     * build [XuMesh.mFaces] based on line from OBJ containing vertex data
     */
    private fun fillFaceList(line: String) {
        val vertexIndices = line.split(DELIMITER).toTypedArray()

        val objFace = Face()
        objFace.fileName = currentTextureName

        if (!(vertexIndices[1].contains("/"))) {
            // vertexIndices[] format: "f vertexIndex1 vertexIndex2 vertexIndex3"
            currentMesh.hasNormalInFace = false
            currentMesh.hasTextureInFace = false

            for (i in 1 until clamp(vertexIndices.size + 1, 2, 4)) {
                objFace.add(
                    FaceElement(Integer.valueOf(vertexIndices[i]) - 1, 0, 0)
                )
            }

        } else {
            // vertexIndices[] format: "f vertexIndex/textureIndex/normalIndex .."
            currentMesh.hasNormalInFace = true
            currentMesh.hasTextureInFace = true

            for (i in 1 until clamp(vertexIndices.size + 1, 2, 4)) {
                val indices = vertexIndices[i].split("/").toTypedArray()

                val vertexIndex =
                    if (indices[0].isNotEmpty()) Integer.valueOf(indices[0]) - 1 else 0

                val textureIndex =
                    if (indices[1].isNotEmpty()) Integer.valueOf(indices[1]) - 1 else 0

                val normalIndex =
                    if (indices[2].isNotEmpty()) Integer.valueOf(indices[2]) - 1 else 0

                objFace.add(FaceElement(vertexIndex, textureIndex, normalIndex))
            }

        }
        currentMesh.mFaces.add(objFace)
    }


    /**
     * obj需解析字段
     */
    private const val MTLLIB = "mtllib"     // obj对应的材质文件

    private const val ANNOTATION = "#"      // 注释

    private const val G = "g"               // 组名称

    private const val O = "o"               // o 对象名称(Object name)

    private const val V = "v "               // 顶点

    private const val VT = "vt"             // 纹理坐标

    private const val VN = "vn"             // 顶点法线

    private const val USEMTL = "usemtl"     // 使用的材质

    private const val F = "f"   // v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3(索引起始于1)

    private const val DELIMITER = " "    // 分隔符

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

}



