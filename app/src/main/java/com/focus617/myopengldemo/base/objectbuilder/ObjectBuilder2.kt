package com.focus617.myopengldemo.base.objectbuilder

import timber.log.Timber

class ObjectBuilder2 {
    private val vertexList = ArrayList<Float>()
    private val indexList = ArrayList<Short>()

    fun appendStar(
        angleNum: Int,  // 星形的锐角个数
        r: Float,       // 内角半径
        R: Float,       // 外角半径
        z: Float,       // z轴基准坐标
        UNIT_SIZE: Float = 1f
    ){

        val tempAngle: Int = 360 / angleNum
        val t = z + UNIT_SIZE * 0.12f

        var index: Short = 0

        //循环生成构成星形各三角形的顶点坐标
        for (angle in 0 until 360 step tempAngle)
        {
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