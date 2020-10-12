package com.focus617.myopengldemo.util

import android.content.Context
import android.text.TextUtils
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

    var objList = ArrayList<ObjInfo>()
    var currentObj = ObjInfo()

    // 存放解析过程的中间数据
    private val verticesList = mutableListOf<String>()
    private val verticesNormalList = mutableListOf<String>()
    private val textureCoordsList = mutableListOf<String>()
    private val facesList = mutableListOf<String>()

    private fun dump(){
        Timber.d("verticesList Size: ${verticesList.size}")
        Timber.d("verticesNormalList Size: ${verticesNormalList.size}")
        Timber.d("textureCoordsList Size: ${textureCoordsList.size}")
        Timber.d("facesList Size: ${facesList.size}")
    }

    fun load(context: Context, objFileName: String) {

        loadFromObjFile(context, objFileName)
        dump()

        fillVertices()
        currentObj.dumpVertexList()

        fillNormals()
        currentObj.dumpNormalList()

        fillTextureCoords()
        currentObj.dumpTextureList()

        analyzeFacesList()
        currentObj.dumpFaceList()

        currentObj.dump()
    }

    private fun loadFromObjFile(context: Context, objFileName: String) {

        Timber.d("loadFromObjFile: $objFileName")

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
                    line.startsWith(O) ->{
                        fillObjName(line)
                    }
                    line.startsWith(VN) -> {
                        verticesNormalList.add(line)
                    }
                    line.startsWith(VT) -> {
                        textureCoordsList.add(line)
                    }
                    line.startsWith(V) -> {
                        verticesList.add(line)
                    }
                    line.startsWith(USEMTL) -> {
                        facesList.add(line)
                    }
                    line.startsWith(F) -> {
                        facesList.add(line)
                    }
                    else ->{
                        Timber.d("dropped: $line")
                    }
                }
            }
            scanner.close()
        } catch (ex: Exception) {
            Timber.e(ex.message.toString())
        }
    }

    // 对象名称
    private fun fillObjName(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return
        currentObj.name = items[1]
    }

    // 材质
    private fun fillMtlLib(line: String) {
        val items = line.split(DELIMITER).toTypedArray()
        if (items.size != 2) return
        if (!TextUtils.isEmpty(items[1])) {
            currentObj.mMtlFileName = items[1]
        }
    }

    /**
     * Uses [verticesList] which is a list of lines from OBJ containing vertex data
     * to create vertex array list
     */
    private fun fillVertices() {
        for (vertex in verticesList) {
            val coordinates = vertex.split(DELIMITER)
            if (coordinates.size != 4) return

            val x = coordinates[1].toFloat()
            val y = coordinates[2].toFloat()
            val z = coordinates[3].toFloat()
            currentObj.mVertexList.add(ObjVertex(x, y, z))
        }
    }

    /**
     * Uses [verticesNormalList] which is a list of lines from OBJ containing vertex normal data
     * to create vertex normal array list
     */
    private fun fillNormals() {
        for (vertex in verticesNormalList) {
            val vectors = vertex.split(DELIMITER)
            if (vectors.size != 4) return

            val x = vectors[1].toFloat()
            val y = vectors[2].toFloat()
            val z = vectors[3].toFloat()
            currentObj.mNormalList.add(ObjNormal(x, y, z))
        }
    }

    /**
     * Uses [textureCoordsList] which is a list of lines from OBJ containing texture coords data
     * to create texture coords array list
     *
     * 这里纹理的Y值，需要(Y = 1-Y0),原因是openGl的纹理坐标系与android的坐标系存在Y值镜像的状态
     */
    private fun fillTextureCoords() {
        for (vertex in textureCoordsList) {
            val coordinates = vertex.split(DELIMITER)
            when(coordinates.size){
                3 -> currentObj.textureDimension = 2
                4 -> currentObj.textureDimension = 3
                !in 3..4 -> continue
            }

            var objTexture = ObjTexture(0f, 0f, 0f)
            objTexture.put(0, coordinates[1].toFloat())

            // 纹理的Y值，需要(Y = 1-Y0)
            objTexture.put(1, 1f - coordinates[2].toFloat())

            if (coordinates.size == 4) {
                objTexture.put(2, coordinates[3].toFloat())
            }
            currentObj.mTextureList.add(objTexture)
        }
    }

    /**
     * Uses [facesList] which is a list of lines from OBJ containing face data
     * to create element list
     */
    private fun analyzeFacesList() {
        Timber.d("analyzeFacesList()")

        var textureFileName: String = ""

        for (face in facesList) {
            val vertexIndices = face.split(DELIMITER).toTypedArray()

            if (vertexIndices[0].startsWith("usemtl")) {

                // 分析 usemtl Texture_n, 切换纹理贴图
                textureFileName = vertexIndices[1]
//                Timber.d("Switch to textureFileName: $textureFileName")

            } else {
                // 分析 f Index
                // 注意：索引起始于1

                if (!(vertexIndices[1].contains("/"))) {
                    // vertexIndices[] format: "f vertexIndex1 vertexIndex2 vertexIndex3"
                    currentObj.hasNormalInFace = false
                    currentObj.hasTextureInFace = false

                    val defaultElement = ObjFaceElement(0, 0, 0)
                    val objFace =
                        ObjFace(textureFileName, defaultElement, defaultElement, defaultElement)

                    for (i in 1 until clamp(vertexIndices.size+1, 2, 4)) {
                        objFace.put(i-1, ObjFaceElement(Integer.valueOf(vertexIndices[i])-1, 0, 0))
                    }
                    currentObj.mFaceList.add(objFace)

                } else {
                    // vertexIndices[] format: "f vertexIndex/textureIndex/normalIndex .."
                    currentObj.hasNormalInFace = true
                    currentObj.hasTextureInFace = true

                    val defaultElement = ObjFaceElement(0, 0, 0)
                    val objFace =
                        ObjFace(textureFileName, defaultElement, defaultElement, defaultElement)

                    for (i in 1 until clamp(vertexIndices.size+1, 2, 4)) {
                        val indices = vertexIndices[i].split("/").toTypedArray()

                        val vertexIndex = if(indices[0].isNotEmpty()) Integer.valueOf(indices[0])-1 else 0

                        val textureIndex = if(indices[1].isNotEmpty()) Integer.valueOf(indices[1])-1 else 0

                        val normalIndex = if(indices[2].isNotEmpty()) Integer.valueOf(indices[2])-1 else 0

                        objFace.put(i-1, ObjFaceElement(vertexIndex, textureIndex, normalIndex))
                    }
                    currentObj.mFaceList.add(objFace)
                }

            }
        }

    }

    companion object {

        /** obj需解析字段 */
        private const val MTLLIB = "mtllib"     // obj对应的材质文件

        private const val ANNOTATION = "#"      // 注释

        private const val G = "g"               // 组名称

        private const val O = "o"               // o 对象名称(Object name)

        private const val V = "v "               // 顶点

        private const val VT = "vt"             // 纹理坐标

        private const val VN = "vn"             // 顶点法线

        private const val USEMTL = "usemtl"     // 使用的材质

        private const val F = "f"   // v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3(索引起始于1)

        private const val  DELIMITER = " "    // 分隔符

        class ObjVertex(var x: Float, var y: Float, var z: Float){
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

        class ObjFaceElement(var vId: Int, var tId: Int, var nId: Int){
            override fun toString() = "($vId, $tId, $nId)"
        }

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

            override fun toString() = "$fileName,[$a, $b, $c]"
        }

        /**
         * mtl文件格式 和 解析后的信息类
         *
         * newmtl Default       # 定义一个名为 'Default'的材质
         *
         * # exponent指定材质的反射指数，定义了反射高光度
         * Ns 96.078431
         * # 材质的环境光
         * Ka 0 0 0
         * # 散射光
         * Kd 0.784314 0.784314 0.784314
         * # 镜面光
         * Ks 0 0 0
         *
         * # 透明度
         * d 1
         *
         * # 为漫反射指定颜色纹理文件
         * map_Kd test_vt.png
         * map_Ka picture1.png #阴影纹理贴图
         * map_Ks picture2.png #高光纹理贴图
         * illum 2 #光照模型
         *
         * #光照模型属性如下：
         * #0. 色彩开，阴影色关
         * #1. 色彩开，阴影色开
         * #2. 高光开
         * #3. 反射开，光线追踪开
         * #4. 透明： 玻璃开 反射：光线追踪开
         * #5. 反射：菲涅尔衍射开，光线追踪开
         * #6. 透明：折射开 反射：菲涅尔衍射关，光线追踪开
         * #7. 透明：折射开 反射：菲涅尔衍射开，光线追踪开
         * #8. 反射开，光线追踪关
         * #9. 透明： 玻璃开 反射：光线追踪关
         * #10. 投射阴影于不可见表面
         */
        class MtlInfo {
            // 材质对象名称
            var name: String? = null

            // 环境光
            var Ka_Color = 0

            // 散射光
            var Kd_Color = 0

            // 镜面光
            var Ks_Color = 0

            // 高光调整参数
            var ns = 0f

            // 溶解度，为0时完全透明，1完全不透明
            var alpha = 1f

            // map_Ka，map_Kd，map_Ks：材质的环境（ambient），散射（diffuse）和镜面（specular）贴图
            var Ka_Texture: String? = null
            var Kd_Texture: String? = null
            var Ks_ColorTexture: String? = null
            var Ns_Texture: String? = null
            var alphaTexture: String? = null
            var bumpTexture: String? = null
        }

        /** obj文件信息类 */
        class ObjInfo {
            // 解析对象名
            var name: String? = null

            // 存放解析出来的 mtl文件名称 和 mtl数据
            var mMtlFileName: String? = null
            val mtlInfo: MtlInfo? = null

            // 存放解析出来的顶点的坐标
            val mVertexList = ArrayList<ObjVertex>()

            //存放解析出来的法线坐标
            val mNormalList = ArrayList<ObjNormal>()

            //存放解析出来的纹理坐标
            val mTextureList = ArrayList<ObjTexture>()

            //存放解析出来面的索引
            val mFaceList = ArrayList<ObjFace>()

            var hasNormalInFace = false
            var hasTextureInFace = false
            var textureDimension = 2

            fun dump(){
                Timber.d("ObjName: $name")
                Timber.d("MtlLib: $mMtlFileName")
                Timber.d("Vertex Size: ${mVertexList.size}")
                Timber.d("Normal Array Size: ${mNormalList.size}")
                Timber.d("Texture Array Size: ${mTextureList.size}")
                Timber.d("Texture Dimension: $textureDimension")
                Timber.d("Index Array Size: ${mFaceList.size}")
            }

            fun dumpVertexList(){
                Timber.d("mVertexArrayList dump:")
                val verticesSize = mVertexList.size
                Timber.d("Vertex Size: $verticesSize")

                if(verticesSize<4){
                    for (i in 0..verticesSize)
                        Timber.d("Vertex[$i]: ${mNormalList[i]}")
                } else {
                    for (i in 0..2)
                        Timber.d("Vertex[$i]: ${mVertexList[i]}")
                    Timber.d("...")
                    for (i in (verticesSize - 3) until verticesSize)
                        Timber.d("Vertex[$i]: ${mVertexList[i]}")
                }
            }

            fun dumpNormalList(){
                Timber.d("mNormalList dump:")
                val normalArraySize = mNormalList.size
                Timber.d("Normal Array Size: $normalArraySize")

                if(normalArraySize<4){
                    for (i in 0..normalArraySize)
                        Timber.d("Normal[$i]: ${mNormalList[i]}")
                } else {
                    for (i in 0..2)
                        Timber.d("Normal[$i]: ${mNormalList[i]}")
                    Timber.d("...")
                    for (i in (normalArraySize - 3) until normalArraySize)
                        Timber.d("Normal[$i]: ${mNormalList[i]}")
                }
            }

            fun dumpTextureList(){
                Timber.d("mTextureArrayList dump:")
                val textureArraySize = mTextureList.size
                Timber.d("Texture Array Size: $textureArraySize")

                if(textureArraySize<4){
                    for (i in 0..textureArraySize)
                        Timber.d("Texture[$i]: ${mTextureList[i]}")
                } else {
                    for (i in 0..2)
                        Timber.d("Texture[$i]: ${mTextureList[i]}")
                    Timber.d("...")
                    for (i in (textureArraySize - 3) until textureArraySize)
                        Timber.d("Texture[$i]: ${mTextureList[i]}")
                }
            }

            fun dumpFaceList(){
                Timber.d("mFaceList dump:")
                val faceArraySize = mFaceList.size
                Timber.d("Face Array Size: $faceArraySize")

                if(faceArraySize<4){
                    for (i in 0..faceArraySize)
                        Timber.d("Face[$i]: ${mFaceList[i]}")
                } else {
                    for (i in 0..2)
                        Timber.d("Face[$i]: ${mFaceList[i]}")
                    Timber.d("...")
                    for (i in (faceArraySize - 3) until faceArraySize)
                        Timber.d("Face[$i]: ${mFaceList[i]}")
                }
            }
        }
    }
}
