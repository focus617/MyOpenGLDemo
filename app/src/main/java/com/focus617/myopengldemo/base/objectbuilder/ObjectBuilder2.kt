package com.focus617.myopengldemo.base.objectbuilder

import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

class ObjectBuilder2 {
    private val vertexList = ArrayList<Float>()
    private val indexList = ArrayList<Short>()
    private var index: Short = 0    // Vertex index

    fun appendTexturedCircle(radius: Float, numPoints: Int, y: Float = 0f, UNIT_SIZE: Float = 1f) {

        var startVertexIndex = index

        // 第一个点: 圆面中心点的x、y、z坐标
        vertexList.add(0f)
        vertexList.add(y)
        vertexList.add(0f)
        // XZ平面的法线向量等于（0，1，0）
        vertexList.add(0f)
        vertexList.add(1f)
        vertexList.add(0f)
        //圆面中心点的顶点纹理坐标
        vertexList.add(0.5f)    //st坐标
        vertexList.add(0.5f)
        index++

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (i in 0 until numPoints) {

            // 第二个点开始，顶点都在圆周上，其x、y、z坐标:
            var angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())
            vertexList.add(radius * UNIT_SIZE * cos(angleInRadians))
            vertexList.add(y)
            vertexList.add(-radius * UNIT_SIZE * sin(angleInRadians))
            // XZ平面的法线向量等于（0，1，0）
            vertexList.add(0f)
            vertexList.add(1f)
            vertexList.add(0f)
            //当前弧度对应的边缘顶点纹理坐标
            vertexList.add(0.5f - 0.5f * cos(angleInRadians))//st坐标
            vertexList.add(0.5f - 0.5f * sin(angleInRadians))

            index++
        }

        // 构造索引
        for (i in 1 until numPoints - 1) {
            indexList.add(startVertexIndex)    // 中心点
            indexList.add((startVertexIndex + i).toShort())
            indexList.add((startVertexIndex + i + 1).toShort())
        }
        // 最后一个特殊三角形，需要循环引用第2个顶点
        indexList.add(startVertexIndex)    // 中心点
        indexList.add((startVertexIndex + numPoints - 1).toShort())
        indexList.add((startVertexIndex + 1).toShort())
    }

    fun appendTexturedOpenCylinder(
        radius: Float,
        height: Float,
        numPoints: Int,
        UNIT_SIZE: Float = 1f
    ) {
        val startVertexIndex = index

        val yStart: Float = -height / 2f
        val yEnd: Float = height / 2f
        val sizew = 1.0f / numPoints    // 纹理水平方向步进值

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (i in 0..numPoints) {
            val angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())

            val xPosition: Float = radius * UNIT_SIZE * cos(angleInRadians)
            val zPosition: Float = radius * UNIT_SIZE * sin(angleInRadians)

            // 第1个点
            vertexList.add(xPosition)
            vertexList.add(yStart)
            vertexList.add(zPosition)
            // 法线向量等于（x，0，z）
            vertexList.add(xPosition)
            vertexList.add(0f)
            vertexList.add(zPosition)
            //圆面中心点的顶点纹理坐标
            vertexList.add(i * sizew)    //st坐标
            vertexList.add(0f)
            index++

            // 第2个点
            vertexList.add(xPosition)
            vertexList.add(yEnd)
            vertexList.add(zPosition)
            vertexList.add(xPosition)
            vertexList.add(0f)
            vertexList.add(zPosition)
            vertexList.add(i * sizew)    //st坐标
            vertexList.add(1f)
            index++
        }

        // 构造索引
        for (i in 0 until numPoints) {
            // 第一个三角形
            indexList.add((startVertexIndex + 2 * i).toShort())
            indexList.add((startVertexIndex + 2 * i + 1).toShort())
            indexList.add((startVertexIndex + 2 * i + 2).toShort())
            // 第二个三角形
            indexList.add((startVertexIndex + 2 * i + 2).toShort())
            indexList.add((startVertexIndex + 2 * i + 1).toShort())
            indexList.add((startVertexIndex + 2 * i + 3).toShort())
        }
        // 最后2个特殊三角形，需要循环引用第1,2个顶点
        indexList.add((startVertexIndex + 2 * numPoints - 2).toShort())
        indexList.add((startVertexIndex + 2 * numPoints - 1).toShort())
        indexList.add(startVertexIndex)

        indexList.add(startVertexIndex)
        indexList.add((startVertexIndex + 2 * numPoints - 1).toShort())
        indexList.add((startVertexIndex + 1).toShort())

    }

    // 圆锥体
    fun appendTexturedCone(
        radius: Float,
        height: Float,
        numPoints: Int,
        UNIT_SIZE: Float = 1f
    ) {

        var startVertexIndex = index

        // 第一个点: 圆面中心点的x、y、z坐标
        vertexList.add(0f)
        vertexList.add(height)
        vertexList.add(0f)
        // XZ平面的法线向量等于（0，1，0）
        vertexList.add(0f)
        vertexList.add(1f)
        vertexList.add(0f)
        //圆面中心点的顶点纹理坐标
        vertexList.add(0.5f)    //st坐标
        vertexList.add(0.5f)
        index++

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (i in 0 until numPoints) {

            // 第二个点开始，顶点都在圆周上，其x、y、z坐标:
            var angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())
            val x = radius * UNIT_SIZE * cos(angleInRadians)
            val y = 0f
            val z = -radius * UNIT_SIZE * sin(angleInRadians)
            vertexList.add(x)
            vertexList.add(y)
            vertexList.add(z)

            // 法线向量:当前的顶点为底面圆周上的点
            //通过3个顶点的坐标求出法向量
            val norXYZ = calConeNormal(
                0f, 0f, 0f,  //底面圆的中心点
                x, y, z,  //当前底面圆周顶点坐标
                0f, height, 0f//圆锥中心最高点坐标
            )
            //将法向量X、Y、Z 3个分量的值
            vertexList.add(norXYZ.x)
            vertexList.add(norXYZ.y)
            vertexList.add(norXYZ.z)

            //当前弧度对应的边缘顶点纹理坐标
            vertexList.add(0.5f - 0.5f * cos(angleInRadians))//st坐标
            vertexList.add(0.5f - 0.5f * sin(angleInRadians))

            index++
        }

        // 构造索引
        for (i in 1 until numPoints - 1) {
            indexList.add(startVertexIndex)    // 中心点
            indexList.add((startVertexIndex + i).toShort())
            indexList.add((startVertexIndex + i + 1).toShort())
        }
        // 最后一个特殊三角形，需要循环引用第2个顶点
        indexList.add(startVertexIndex)    // 中心点
        indexList.add((startVertexIndex + numPoints - 1).toShort())
        indexList.add((startVertexIndex + 1).toShort())
    }

    //计算圆锥面指定棱顶点法向量的方法
    private fun calConeNormal(
        x0: Float, y0: Float, z0: Float,  //A，中心点(底面圆的圆心)
        x1: Float, y1: Float, z1: Float,  //B，底面圆上的某一点
        x2: Float, y2: Float, z2: Float   //C，圆锥中心最高点
    ): Vector {
        val a = Vector(x1 - x0, y1 - y0, z1 - z0) //向量AB
        val b = Vector(x2 - x0, y2 - y0, z2 - z0) //向量AC
        val c = Vector(x2 - x1, y2 - y1, z2 - z1) //向量BC
        //先求平面ABC的法向量k
        val k = a.crossProduct(b)
        //将c和k做叉乘，得出所求向量d
        val d = c.crossProduct(k)
        //返回规格化后的法向量
        return d.normalize()
    }


    fun appendTexturedBall(radius: Float, UNIT_SIZE: Float = 1f) {

        val sizew = 1.0f / (360 / angleSpan)    // 纹理水平方向步进值
        val sizeh = 1.0f / (180 / angleSpan)     // 纹理垂直方向步进值

        for ((row, vAngle) in (90 downTo -90 step angleSpan).withIndex()) {
            for ((col, hAngle) in (360 downTo 0 step angleSpan).withIndex()) {
                // 纵向横向各到一个角度后计算对应的此点在球面上的坐标
                // 每行列一个矩形(1:左上，2：左下，3：右下， 4：右上）
                var xozLength: Double = radius * UNIT_SIZE * cos(Math.toRadians(vAngle.toDouble()))
                val x1 = (xozLength * cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z1 = (xozLength * sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y1 = (radius * UNIT_SIZE * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                xozLength = radius * UNIT_SIZE * cos(Math.toRadians(vAngle - angleSpan.toDouble()))
                val x2 = (xozLength * cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z2 = (xozLength * sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y2 =
                    (radius * UNIT_SIZE * sin(Math.toRadians(vAngle - angleSpan.toDouble()))).toFloat()

                xozLength = radius * UNIT_SIZE * cos(Math.toRadians(vAngle - angleSpan.toDouble()))
                val x3 = (xozLength * cos(Math.toRadians(hAngle - angleSpan.toDouble()))).toFloat()
                val z3 = (xozLength * sin(Math.toRadians(hAngle - angleSpan.toDouble()))).toFloat()
                val y3 =
                    (radius * UNIT_SIZE * sin(Math.toRadians(vAngle - angleSpan.toDouble()))).toFloat()

                xozLength = radius * UNIT_SIZE * cos(Math.toRadians(vAngle.toDouble()))
                val x4 = (xozLength * cos(Math.toRadians(hAngle - angleSpan.toDouble()))).toFloat()
                val z4 = (xozLength * sin(Math.toRadians(hAngle - angleSpan.toDouble()))).toFloat()
                val y4 = (radius * UNIT_SIZE * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                //计算纹理:得到row行col列位于小矩形的左上角的，第1点的纹理坐标值
                val s = col * sizew
                val t = row * sizeh

                // 顶点数组由两个三角形构成，共六个顶点，每个顶点占用s,t 2个纹理坐标
                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
                //构建第一三角形
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                // 球体的法线向量等同于其顶点坐标
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                // 纹理坐标
                vertexList.add(s)
                vertexList.add(t)
                // 索引
                indexList.add(index++)

                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                vertexList.add(s)
                vertexList.add(t + sizeh)
                indexList.add(index++)

                vertexList.add(x4)
                vertexList.add(y4)
                vertexList.add(z4)
                vertexList.add(x4)
                vertexList.add(y4)
                vertexList.add(z4)
                vertexList.add(s + sizew)
                vertexList.add(t)
                indexList.add(index++)

                //构建第二三角形
                vertexList.add(x4)
                vertexList.add(y4)
                vertexList.add(z4)
                vertexList.add(x4)
                vertexList.add(y4)
                vertexList.add(z4)
                vertexList.add(s + sizew)
                vertexList.add(t)
                indexList.add(index++)

                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                vertexList.add(s)
                vertexList.add(t + sizeh)
                indexList.add(index++)

                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                vertexList.add(s + sizew)
                vertexList.add(t + sizeh)
                indexList.add(index++)
            }
        }
    }

    //对矩形自动切分纹理产生纹理数组的方法
    //bw:列数
    //bh:行数
//    fun generateTexCoor(bw: Int, bh: Int): FloatArray {
//        val result = FloatArray(bw * bh * 6 * 2)
//        val sizew = 1.0f / bw
//        val sizeh = 1.0f / bh
//        var c = 0
//        for (i in 0 until bh) {
//            for (j in 0 until bw) {
//                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
//                val s = j * sizew
//                val t = i * sizeh               //得到i行j列小矩形的左上点的纹理坐标值
//                result[c++] = s
//                result[c++] = t                       //该矩形左上点纹理坐标值
//                result[c++] = s
//                result[c++] = t + sizeh               //该矩形左下点纹理坐标值
//                result[c++] = s + sizew
//                result[c++] = t                       //该矩形右上点纹理坐标值
//                result[c++] = s + sizew
//                result[c++] = t                       //该矩形右上点纹理坐标值
//                result[c++] = s
//                result[c++] = t + sizeh               //该矩形左下点纹理坐标值
//                result[c++] = s + sizew
//                result[c++] = t + sizeh               //该矩形右下点纹理坐标值
//            }
//        }
//        return result
//    }
//
//    fun generateBallTexCoor(): FloatArray = generateTexCoor(360 / angleSpan, 180 / angleSpan)

    fun appendStar(
        angleNum: Int,  // 星形的锐角个数
        r: Float,       // 内角半径
        R: Float,       // 外角半径
        z: Float,       // z轴基准坐标
        UNIT_SIZE: Float = 1f
    ) {

        val tempAngle: Int = 360 / angleNum
        //循环生成构成星形各三角形的顶点坐标
        for (angle in 0 until 360 step tempAngle) {
            //第一个三角形
            //第一个点的x、y、z坐标
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(z)
            // XY平面的法线向量等于（0，0，1）
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(1f)
            indexList.add(index++)
            //第二个点的x、y、z坐标
            vertexList.add((R * UNIT_SIZE * Math.cos(Math.toRadians(angle.toDouble()))).toFloat())
            vertexList.add((R * UNIT_SIZE * Math.sin(Math.toRadians(angle.toDouble()))).toFloat())
            vertexList.add(z)
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(1f)
            indexList.add(index++)
            //第三个点的x、y、z坐标
            vertexList.add((r * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add((r * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add(z)
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(1f)
            indexList.add(index++)

            //第二个三角形
            //第一个中心点的x、y、z坐标
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(z)
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(1f)
            indexList.add(index++)
            //第二个点的x、y、z坐标
            vertexList.add((r * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add((r * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add(z)
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(1f)
            indexList.add(index++)
            //第三个点的x、y、z坐标
            vertexList.add((R * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle).toDouble()))).toFloat())
            vertexList.add((R * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle).toDouble()))).toFloat())
            vertexList.add(z)
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(1f)
            indexList.add(index++)
        }
    }

    private val angleSpan = 10      // 将球进行单位切分的角度
    fun appendBall(radius: Float, UNIT_SIZE: Float = 1f) {

        for (vAngle in -90 until 90 step angleSpan) {
            for (hAngle in 0..360 step angleSpan) {
                // 纵向横向各到一个角度后计算对应的此点在球面上的坐标
                var xozLength: Double = radius * UNIT_SIZE * cos(Math.toRadians(vAngle.toDouble()))
                val x0 = (xozLength * cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y0 = (xozLength * sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z0 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                xozLength = radius * UNIT_SIZE * cos(Math.toRadians(vAngle.toDouble()))
                val x1 = (xozLength * cos(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val y1 = (xozLength * sin(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val z1 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                xozLength = radius * UNIT_SIZE * cos(Math.toRadians(vAngle + angleSpan.toDouble()))
                val x2 = (xozLength * cos(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val y2 = (xozLength * sin(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val z2 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle + angleSpan.toDouble()))).toFloat()

                xozLength = radius * UNIT_SIZE * cos(Math.toRadians(vAngle + angleSpan.toDouble()))
                val x3 = (xozLength * cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y3 = (xozLength * sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z3 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle + angleSpan.toDouble()))).toFloat()

                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
                // 球体的法线向量等同于其顶点坐标
                //构建第一三角形
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                indexList.add(index++)

                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                indexList.add(index++)

                vertexList.add(x0)
                vertexList.add(y0)
                vertexList.add(z0)
                vertexList.add(x0)
                vertexList.add(y0)
                vertexList.add(z0)
                indexList.add(index++)

                //构建第二三角形
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                indexList.add(index++)

                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                indexList.add(index++)

                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                indexList.add(index++)
            }
        }
    }

    fun buildTexturedData(): GeneratedData {

        val numVertices = vertexList.size / TEXTURE_VERTEX_ATTRIBUTE_SIZE

        val vertexArray = FloatArray(numVertices * TEXTURE_VERTEX_ATTRIBUTE_SIZE)
        val indexArray = ShortArray(indexList.size)

        Timber.d(
            "buildTexturedBallData(): Size - V:${vertexArray.size}, I:${indexArray.size}}"
        )

        // 将构造的顶点列表转存为顶点数组
        for (i in 0 until numVertices) {
            // copy vertex and normal
            for (j in 0 until TEXTURE_VERTEX_ATTRIBUTE_SIZE) {
                vertexArray[i * TEXTURE_VERTEX_ATTRIBUTE_SIZE + j] =
                    vertexList[i * TEXTURE_VERTEX_ATTRIBUTE_SIZE + j]
            }
        }

        // 将构造的顶点列表转存为顶点索引数组
        for (i in 0 until indexList.size) {
            indexArray[i] = indexList[i]
        }

        return GeneratedData(numVertices, vertexArray, indexArray)
    }

    fun buildData(): GeneratedData {

        val vertexArray = FloatArray(vertexList.size)
        val indexArray = ShortArray(indexList.size)

        // 将构造的顶点列表转存为顶点数组和顶点索引数组
        val numVertices = vertexList.size / VERTEX_ATTRIBUTE_SIZE
        for (i in 0 until numVertices) {
            for (j in 0 until VERTEX_ATTRIBUTE_SIZE) {
                vertexArray[i * VERTEX_ATTRIBUTE_SIZE + j] =
                    vertexList[i * VERTEX_ATTRIBUTE_SIZE + j]
            }
            indexArray[i] = indexList[i]
        }
        return GeneratedData(numVertices, vertexArray, indexArray)
    }

    companion object {

        class GeneratedData(
            val numVertices: Int,           // 顶点数量
            val vertexArray: FloatArray,    // 顶点数组
            val indexArray: ShortArray      // 顶点索引数组
        )

        // 顶点坐标的每个属性的Size
        private const val VERTEX_POS_SIZE = 3            //x,y,z
        private const val VERTEX_NORMAL_SIZE = 3         //NX, NY, NZ
        private const val VERTEX_TEXCOORDO_SIZE = 2      //s and t

        // the following 4 defines are used to determine the locations
        // of various attributes if vertex data are stored as an array
        //of structures
        private const val VERTEX_POS_OFFSET = 0
        private const val VERTEX_NORMAL_OFFSET = VERTEX_POS_SIZE * Float.SIZE_BYTES
        private const val VERTEX_TEX_COORDO_OFFSET =
            (VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE) * Float.SIZE_BYTES

        private const val VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE

        private const val TEXTURE_VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORDO_SIZE
    }
}