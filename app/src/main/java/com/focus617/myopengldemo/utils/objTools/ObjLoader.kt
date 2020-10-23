package com.focus617.myopengldemo.utils.objTools

import android.content.Context
import android.text.TextUtils
import com.focus617.myopengldemo.utils.Vector.Companion.calTriangleNormal
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


/**
 * @description Wavefront Obj 3D模型文件解析类
 */
object ObjLoader {

    //模型文件所在目录
    private lateinit var directory: String

    private lateinit var mObjInfo: ObjInfo

    private var currentMaterialName: String = DEFAULT_GROUP_NAME     // 存放解析出来的face当前使用的texture
    private var currentIndexList: ArrayList<Int>? = null
    private var currentVertexList: ArrayList<Float>? = null
    private var hasFace = false

    fun dumpObjInfo() {
        Timber.d("dumpObjInfo()")
        mObjInfo.dump()
        mObjInfo.dumpVertices()
        mObjInfo.dumpNormals()
        mObjInfo.dumpTextureCoords()
        mObjInfo.dumpFaces()
        mObjInfo.dumpMaterials()
    }

    /**
     * 加载并分析Obj文件，构造 Meshes 和 Materials
     * @param context   Context
     * @param objFilePathName assets的obj文件路径
     * @return
     */
    fun load(context: Context, objFilePathName: String): ObjInfo {

        Timber.d("loadFromObjFile(): $objFilePathName")

        if (objFilePathName.isEmpty() or TextUtils.isEmpty(objFilePathName)) {
            Timber.w("Obj File doesn't exist")
        }

        directory =
            if (objFilePathName.contains('/'))
                objFilePathName.substring(0, objFilePathName.lastIndexOf('/'))
            else "."

        parse(context, objFilePathName)

        dumpObjInfo()

        return mObjInfo
    }

    private fun parse(context: Context, objFilePathName: String): ObjInfo {

        mObjInfo = ObjInfo()
        mObjInfo.name = objFilePathName

        try {
            val scanner = Scanner(context.assets.open(objFilePathName))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine().trim()

                when {
                    line.isEmpty() -> continue

                    line.startsWith(ANNOTATION) -> continue  // 注释行

                    line.startsWith(MTLLIB) -> {
                        fillMtlLib(line)
                        if (mObjInfo.mMtlFileName != null) {
                            MtlLoader.load(context, mObjInfo.mMtlFileName!!, mObjInfo.mMaterials)
                        }
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
                        switchUseMtl(line)
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
        return mObjInfo
    }


    // 对象名称
    private fun fillObjName(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return
        mObjInfo.name = items[1]
    }

    // 材质
    private fun fillMtlLib(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return
        if (!TextUtils.isEmpty(items[1])) {
            mObjInfo.mMtlFileName = "$directory/${items[1]}"
        }
    }

    /**
     * build [ObjInfo.mVertices] based on line from OBJ containing vertex data
     */
    private fun fillVertexList(line: String) {
        val coordinates = line.split(DELIMITER)
        if (coordinates.size != 4) return

        val x = coordinates[1].toFloat()
        val y = coordinates[2].toFloat()
        val z = coordinates[3].toFloat()
        mObjInfo.mVertices.add(ObjVertex(x, y, z))
    }

    /**
     * build [ObjInfo.mNormals] based on line from OBJ containing vertex data
     */
    private fun fillNormalList(line: String) {
        val vectors = line.split(DELIMITER)
        if (vectors.size != 4) return

        val x = vectors[1].toFloat()
        val y = vectors[2].toFloat()
        val z = vectors[3].toFloat()
        mObjInfo.mNormals.add(ObjNormal(x, y, z))
    }

    /**
     * build [ObjInfo.mTextureCoords] based on line from OBJ containing vertex data
     *
     * 这里纹理的Y值，需要(Y = 1-Y0),原因是openGl的纹理坐标系与android的坐标系存在Y值镜像的状态
     */
    private fun fillTextureCoordList(line: String) {
        val coordinates = line.split(DELIMITER)
//        if (coordinates.size !in 3..4 )  return
        when (coordinates.size) {
            3 -> mObjInfo.textureDimension = 2
            4 -> mObjInfo.textureDimension = 3
            !in 3..4 -> return
        }

        val objTexture = ObjTexture(0f, 0f, 0f)
        objTexture.put(0, coordinates[1].toFloat())

        // 纹理的Y值，需要(Y = 1-Y0)
        objTexture.put(1, 1f - coordinates[2].toFloat())

        if (coordinates.size == 4) {
            objTexture.put(2, coordinates[3].toFloat())
        }
        mObjInfo.mTextureCoords.add(objTexture)
    }

    private fun switchUseMtl(line: String) {
        val textureName = line.split(DELIMITER)
        if (textureName.size != 2) return

        // TODO: Need save old faceList to HashMap?

        // Get new material name
        currentMaterialName = textureName[1]
        Timber.d("switchUseMtl(): $currentMaterialName")

        if (mObjInfo.mIndices.containsKey(currentMaterialName)) {
            // switch to old FaceList based on switched material name
            currentIndexList = mObjInfo.mIndices[currentMaterialName]
            currentVertexList = mObjInfo.mFinalVertices[currentMaterialName]
            Timber.d("switchUseMtl(): Reuse existing FaceList '$currentMaterialName'")
            Timber.d("switchUseMtl(): size = ${currentIndexList!!.size}")

        } else {
            // create a new FaceList
            currentIndexList = ArrayList<Int>()
            currentVertexList = ArrayList<Float>()
            mObjInfo.mIndices[currentMaterialName] = currentIndexList!!
            mObjInfo.mFinalVertices[currentMaterialName] = currentVertexList!!
            Timber.d(
                "switchUseMtl(): Create new IndexList and VertexList for '$currentMaterialName'"
            )
        }
    }

    /**
     * build [ObjInfo.mIndices] based on line from OBJ containing vertex data
     */
    private fun fillFaceList(line: String) {

        val vertexIndices = line.split(DELIMITER).toTypedArray()
        if (vertexIndices.size != 4) return

        if (currentIndexList == null) {
            Timber.d("fillFaceList(): this Obj hasn't 'USEMTL'! ")

            // create a new FaceList
            currentIndexList = ArrayList<Int>()
            currentVertexList = ArrayList<Float>()
            currentMaterialName = DEFAULT_GROUP_NAME
            mObjInfo.mIndices[currentMaterialName] = currentIndexList!!
            mObjInfo.mFinalVertices[currentMaterialName] = currentVertexList!!
            Timber.d(
                "fillFaceList(): Create new IndexList and VertexList for '$currentMaterialName'"
            )
        }

        if (!(vertexIndices[1].contains("/"))) {
            // vertexIndices[] format: "f vertexIndex1 vertexIndex2 vertexIndex3"
            mObjInfo.hasNormalInFace = false
            mObjInfo.hasTextureInFace = false

            var vertexIndex = Integer.valueOf(vertexIndices[1]) - 1
            val x0 = mObjInfo.mVertices[vertexIndex].x
            val y0 = mObjInfo.mVertices[vertexIndex].y
            val z0 = mObjInfo.mVertices[vertexIndex].z

            vertexIndex = Integer.valueOf(vertexIndices[2]) - 1
            val x1 = mObjInfo.mVertices[vertexIndex].x
            val y1 = mObjInfo.mVertices[vertexIndex].y
            val z1 = mObjInfo.mVertices[vertexIndex].z

            vertexIndex = Integer.valueOf(vertexIndices[3]) - 1
            val x2 = mObjInfo.mVertices[vertexIndex].x
            val y2 = mObjInfo.mVertices[vertexIndex].y
            val z2 = mObjInfo.mVertices[vertexIndex].z

            //通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
            val vNormal = calTriangleNormal(
                x0, y0, z0,
                x1, y1, z1,
                x2, y2, z2
            )

            currentVertexList!!.add(x0)
            currentVertexList!!.add(y0)
            currentVertexList!!.add(z0)
            currentVertexList!!.add(vNormal.x)
            currentVertexList!!.add(vNormal.y)
            currentVertexList!!.add(vNormal.z)
            currentVertexList!!.add(0f)
            currentVertexList!!.add(0f)

            var lastIndex = currentIndexList!!.size
            currentIndexList!!.add(lastIndex)

            currentVertexList!!.add(x1)
            currentVertexList!!.add(y1)
            currentVertexList!!.add(z1)
            currentVertexList!!.add(vNormal.x)
            currentVertexList!!.add(vNormal.y)
            currentVertexList!!.add(vNormal.z)
            currentVertexList!!.add(0f)
            currentVertexList!!.add(0f)

            lastIndex = currentIndexList!!.size
            currentIndexList!!.add(lastIndex)

            currentVertexList!!.add(x2)
            currentVertexList!!.add(y2)
            currentVertexList!!.add(z2)
            currentVertexList!!.add(vNormal.x)
            currentVertexList!!.add(vNormal.y)
            currentVertexList!!.add(vNormal.z)
            currentVertexList!!.add(0f)
            currentVertexList!!.add(0f)

            lastIndex = currentIndexList!!.size
            currentIndexList!!.add(lastIndex)


        } else {
            // vertexIndices[] format: "f vertexIndex/textureIndex/normalIndex .."
//            mObjInfo.hasNormalInFace = true
//            mObjInfo.hasTextureInFace = true

            for (i in 1..3) {
                val indices = vertexIndices[i].split("/").toTypedArray()

                val vertexIndex =
                    if (indices[0].isNotEmpty()) Integer.valueOf(indices[0]) - 1 else 0

                val textureIndex =
                    if (indices[1].isNotEmpty()) Integer.valueOf(indices[1]) - 1 else 0

                val normalIndex =
                    if (indices[2].isNotEmpty()) Integer.valueOf(indices[2]) - 1 else 0

                currentVertexList!!.add(mObjInfo.mVertices[vertexIndex].x)
                currentVertexList!!.add(mObjInfo.mVertices[vertexIndex].y)
                currentVertexList!!.add(mObjInfo.mVertices[vertexIndex].z)

                if(normalIndex == 0){
                    currentVertexList!!.add(0f)
                    currentVertexList!!.add(0f)
                    currentVertexList!!.add(0f)
                }
                else {
                    mObjInfo.hasNormalInFace = true

                    currentVertexList!!.add(mObjInfo.mNormals[normalIndex].x)
                    currentVertexList!!.add(mObjInfo.mNormals[normalIndex].y)
                    currentVertexList!!.add(mObjInfo.mNormals[normalIndex].z)
                }

                if(textureIndex == 0){
                    currentVertexList!!.add(0f)
                    currentVertexList!!.add(0f)
                    currentVertexList!!.add(0f)
                }
                else {
                    mObjInfo.hasTextureInFace = true

                    currentVertexList!!.add(mObjInfo.mTextureCoords[textureIndex].s)
                    currentVertexList!!.add(mObjInfo.mTextureCoords[textureIndex].t)
                    if (mObjInfo.textureDimension == 3) {
                        currentVertexList!!.add(mObjInfo.mTextureCoords[textureIndex].w)
                    } else {
                        currentVertexList!!.add(0f)
                    }
                }

                val lastIndex = currentIndexList!!.size
                currentIndexList!!.add(lastIndex)
            }

        }
        hasFace = true
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

    private val DELIMITER = Regex("[ ]+")    // 分隔符


}



