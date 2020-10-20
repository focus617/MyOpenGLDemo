package com.focus617.myopengldemo.base.objectbuilder

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
            indexList.add(index++)

            // 第二个点的x、y、z坐标:
            var angleInRadians =
                (Math.PI.toFloat() * 2f) * (i.toFloat() / numPoints.toFloat())
            vertexList.add(radius * UNIT_SIZE * cos(angleInRadians))
            vertexList.add(0f)
            vertexList.add(radius * UNIT_SIZE * sin(angleInRadians))
            indexList.add(index++)

            //第三个点的x、y、z坐标
            angleInRadians =
                (Math.PI.toFloat() * 2f) * ((i + 1).toFloat() / numPoints.toFloat())
            vertexList.add(radius * UNIT_SIZE * cos(angleInRadians))
            vertexList.add(0f)
            vertexList.add(radius * UNIT_SIZE * sin(angleInRadians))
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

        val t = z + UNIT_SIZE * 0.12f

        val tempAngle: Int = 360 / angleNum
        //循环生成构成星形各三角形的顶点坐标
        for (angle in 0 until 360 step tempAngle) {
            //第一个三角形
            //第一个点的x、y、z坐标
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(t)
            indexList.add(index++)
            //第二个点的x、y、z坐标
            vertexList.add((R * UNIT_SIZE * Math.cos(Math.toRadians(angle.toDouble()))).toFloat())
            vertexList.add((R * UNIT_SIZE * Math.sin(Math.toRadians(angle.toDouble()))).toFloat())
            vertexList.add(z)
            indexList.add(index++)
            //第三个点的x、y、z坐标
            vertexList.add((r * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add((r * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add(z)
            indexList.add(index++)

            //第二个三角形
            //第一个中心点的x、y、z坐标
            vertexList.add(0f)
            vertexList.add(0f)
            vertexList.add(t)
            indexList.add(index++)
            //第二个点的x、y、z坐标
            vertexList.add((r * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add((r * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle / 2).toDouble()))).toFloat())
            vertexList.add(z)
            indexList.add(index++)
            //第三个点的x、y、z坐标
            vertexList.add((R * UNIT_SIZE * Math.cos(Math.toRadians((angle + tempAngle).toDouble()))).toFloat())
            vertexList.add((R * UNIT_SIZE * Math.sin(Math.toRadians((angle + tempAngle).toDouble()))).toFloat())
            vertexList.add(z)
            indexList.add(index++)
        }
    }

    fun appendBall(radius: Float, UNIT_SIZE: Float = 1f) {

        val angleSpan = 10      // 将球进行单位切分的角度

        for (vAngle in -90 until 90 step angleSpan) {
            for (hAngle in 0..360 step angleSpan) {
                // 纵向横向各到一个角度后计算对应的此点在球面上的坐标
                val x0 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle.toDouble()))
                        * cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y0 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle.toDouble()))
                        * sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z0 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                val x1 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle.toDouble()))
                        * cos(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val y1 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle.toDouble()))
                        * sin(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val z1 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                val x2 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle + angleSpan.toDouble()))
                        * cos(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val y2 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle + angleSpan.toDouble()))
                        * sin(Math.toRadians(hAngle + angleSpan.toDouble()))).toFloat()
                val z2 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle + angleSpan.toDouble()))).toFloat()

                val x3 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle + angleSpan.toDouble()))
                        * cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y3 = (radius * UNIT_SIZE
                        * cos(Math.toRadians(vAngle + angleSpan.toDouble()))
                        * sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z3 = (radius * UNIT_SIZE
                        * sin(Math.toRadians(vAngle + angleSpan.toDouble()))).toFloat()

                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                indexList.add(index++)

                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                indexList.add(index++)

                vertexList.add(x0)
                vertexList.add(y0)
                vertexList.add(z0)
                indexList.add(index++)

                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                indexList.add(index++)

                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                indexList.add(index++)

                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                indexList.add(index++)
            }
        }
    }

    fun buildData(): GeneratedData {

        val vertexArray = FloatArray(vertexList.size)
        val indexArray = ShortArray(indexList.size)

        // 将构造的顶点列表转存为顶点数组和顶点索引数组
        val numVertices = vertexList.size / 3
        for (i in 0 until numVertices) {
            vertexArray[i * 3] = vertexList[i * 3]
            vertexArray[i * 3 + 1] = vertexList[i * 3 + 1]
            vertexArray[i * 3 + 2] = vertexList[i * 3 + 2]

            indexArray[i] = indexList[i]
        }
        return GeneratedData(vertexArray, indexArray)
    }

    companion object {

        class GeneratedData(
            val vertexArray: FloatArray,    // 顶点数组
            val indexArray: ShortArray      // 顶点索引数量
        )
    }
}