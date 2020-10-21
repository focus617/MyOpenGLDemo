package com.focus617.myopengldemo.base.objectbuilder

import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

class ObjectBuilder2 {
    private val vertexList = ArrayList<Float>()
    private val indexList = ArrayList<Short>()
    private var index: Short = 0    // Vertex index

    fun appendCircle(radius: Float, numPoints: Int, UNIT_SIZE: Float = 1f) {

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (i in 0..numPoints) {

            // 第一个点的x、y、z坐标: Center point of fan
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(0f)
            // XZ平面的法线向量等于（0，1，0）
            vertexList.add(0f)
            vertexList.add(1f)
            vertexList.add(0f)
            indexList.add(index++)

            // 第二个点的x、y、z坐标:
            var angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())
            vertexList.add(radius * UNIT_SIZE * cos(angleInRadians))
            vertexList.add(0f)
            vertexList.add(radius * UNIT_SIZE * sin(angleInRadians))
            // XZ平面的法线向量等于（0，1，0）
            vertexList.add(0f)
            vertexList.add(1f)
            vertexList.add(0f)
            indexList.add(index++)

            //第三个点的x、y、z坐标
            angleInRadians =
                (Math.PI.toFloat() * 2f) * ((i + 1).toFloat() / numPoints.toFloat())
            vertexList.add(radius * UNIT_SIZE * cos(angleInRadians))
            vertexList.add(0f)
            vertexList.add(radius * UNIT_SIZE * sin(angleInRadians))
            // XZ平面的法线向量等于（0，1，0）
            vertexList.add(0f)
            vertexList.add(1f)
            vertexList.add(0f)
            indexList.add(index++)
        }
    }

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

    fun appendTexturedBall(radius: Float, UNIT_SIZE: Float = 1f) {
        var col = 0     // 纹理列数

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

    //自动切分纹理产生纹理数组的方法
    //bw:列数
    //bh:行数
    fun generateTexCoor(bw: Int, bh: Int): FloatArray {
        val result = FloatArray(bw * bh * 6 * 2)
        val sizew = 1.0f / bw
        val sizeh = 1.0f / bh
        var c = 0
        for (i in 0 until bh) {
            for (j in 0 until bw) {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                val s = j * sizew
                val t = i * sizeh               //得到i行j列小矩形的左上点的纹理坐标值
                result[c++] = s
                result[c++] = t                       //该矩形左上点纹理坐标值
                result[c++] = s
                result[c++] = t + sizeh               //该矩形左下点纹理坐标值
                result[c++] = s + sizew
                result[c++] = t                       //该矩形右上点纹理坐标值
                result[c++] = s + sizew
                result[c++] = t                       //该矩形右上点纹理坐标值
                result[c++] = s
                result[c++] = t + sizeh               //该矩形左下点纹理坐标值
                result[c++] = s + sizew
                result[c++] = t + sizeh               //该矩形右下点纹理坐标值
            }
        }
        return result
    }

    fun generateBallTexCoor(): FloatArray = generateTexCoor(360 / angleSpan, 180 / angleSpan)

    fun buildTexturedBallData(): GeneratedData {

        val numVertices = vertexList.size / TEXTURE_BALL_VERTEX_ATTRIBUTE_SIZE

        val vertexArray = FloatArray(numVertices * TEXTURE_BALL_VERTEX_ATTRIBUTE_SIZE)
        val indexArray = ShortArray(indexList.size)

        Timber.d(
            "buildTexturedBallData(): Size - V:${vertexArray.size}, I:${indexArray.size}}"
        )

        // 将构造的顶点列表转存为顶点数组和顶点索引数组

        for (i in 0 until numVertices) {
            // copy vertex and normal
            for (j in 0 until TEXTURE_BALL_VERTEX_ATTRIBUTE_SIZE) {
                vertexArray[i * TEXTURE_BALL_VERTEX_ATTRIBUTE_SIZE + j] =
                    vertexList[i * TEXTURE_BALL_VERTEX_ATTRIBUTE_SIZE + j]
            }

            // copy index
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

        private const val TEXTURE_BALL_VERTEX_ATTRIBUTE_SIZE =
            VERTEX_POS_SIZE + VERTEX_NORMAL_SIZE + VERTEX_TEXCOORDO_SIZE
    }
}