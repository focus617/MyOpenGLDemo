package com.focus617.myopengldemo.objects.particles

import android.graphics.Bitmap
import android.graphics.Color
import com.focus617.myopengldemo.base.objectbuilder.VertexBuffer
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.programs.particles.HeightmapShaderProgram
import com.focus617.myopengldemo.util.Geometry
import com.focus617.myopengldemo.util.Vector
import com.focus617.myopengldemo.util.Geometry.Point
import timber.log.Timber

class Heightmap(bitmap: Bitmap) {
    private val width: Int = bitmap.width
    private val height: Int = bitmap.height

    private val vertexBuffer: VertexBuffer
    private val numElements: Int

    init {
        Timber.d(
            "Build VertexBuffer(Bitmap width:${width},  height:${height})"
        )
        if (width * height > 65536) {
            throw RuntimeException("Heightmap is too large for the index buffer.")
        }
        numElements = calculateNumElements()
        Timber.d("\tnumElements:${numElements}")

        val vertices: FloatArray = loadBitmapData(bitmap)
        val indices: ShortArray = createIndexData()
        Timber.d("Build VertexBuffer: vertices(size=${vertices.size})")
        for (i in 0..11) Timber.d("VertexBuffer $i : ${vertices[i]}")

        Timber.d("Build VertexBuffer: indices(size=${indices.size})")
        for (i in 0..11) Timber.d("VertexBuffer $i : ${indices[i]}")

        vertexBuffer = VertexBuffer.build(vertices, width*height, indices)
    }


    /**
     * Copy the heightmap data into a vertex buffer object.
     */
    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        val heightmapVertices = FloatArray(width * height * VERTEX_TOTAL_COMPONENT_COUNT)

        var offset = 0
        for (row in 0 until height) {
            for (col in 0 until width) {
                // The heightmap will lie flat on the XZ plane and centered
                // around (0, 0), with the bitmap width mapped to X and the
                // bitmap height mapped to Z, and Y representing the height. We
                // assume the heightmap is grayscale, and use the value of the
                // red color to determine the height.

//                val xPosition = (col.toFloat() / (width - 1).toFloat()) - 0.5f
//                val yPosition =
//                    Color.red(pixels[row * height + col]).toFloat() / 255.toFloat()
//                val zPosition = (row.toFloat() / (height - 1).toFloat()) - 0.5f
//
//                heightmapVertices[offset++] = xPosition
//                heightmapVertices[offset++] = yPosition
//                heightmapVertices[offset++] = zPosition

                val point = getPoint(pixels, row, col)

                heightmapVertices[offset++] = point.x
                heightmapVertices[offset++] = point.y
                heightmapVertices[offset++] = point.z

                val top: Point = getPoint(pixels, row - 1, col)
                val left: Point = getPoint(pixels, row, col - 1)
                val right: Point = getPoint(pixels, row, col + 1)
                val bottom: Point = getPoint(pixels, row + 1, col)

                val rightToLeft: Vector = Vector.vectorBetween(right, left)
                val topToBottom: Vector = Vector.vectorBetween(top, bottom)
                val normal: Vector = rightToLeft.crossProduct(topToBottom).normalize()

                heightmapVertices[offset++] = normal.x
                heightmapVertices[offset++] = normal.y
                heightmapVertices[offset++] = normal.z

                // Texture coordinates
                heightmapVertices[offset++] = point.x * 50f
                heightmapVertices[offset++] = point.z * 50f
            }
        }
        return heightmapVertices
    }

    /**
     * Returns a point at the expected position given by row and col, but if the
     * position is out of bounds, then it clamps the position and uses the
     * clamped position to read the height. For example, calling with row = -1
     * and col = 5 will set the position as if the point really was at -1 and 5,
     * but the height will be set to the heightmap height at (0, 5), since (-1,
     * 5) is out of bounds. This is useful when we're generating normals, and we
     * need to read the heights of neighbouring points.
     */
    private fun getPoint(pixels: IntArray, row: Int, col: Int): Point {
        var row = row
        var col = col

        val x = col.toFloat() / (width - 1).toFloat() - 0.5f
        val z = row.toFloat() / (height - 1).toFloat() - 0.5f

        row = clamp(row, 0, width - 1)
        col = clamp(col, 0, height - 1)

        val y = Color.red(pixels[row * height + col]).toFloat() / 255.toFloat()

        return Point(x, y, z)
    }

    private fun clamp(value: Int, min: Int, max: Int): Int {
        return kotlin.math.max(min, kotlin.math.min(max, value))
    }

    private fun calculateNumElements(): Int {
        // There should be 2 triangles for every group of 4 vertices, so a
        // heightmap of, say, 10x10 pixels would have 9x9 groups, with 2
        // triangles per group and 3 vertices per triangle for a total of (9 x 9
        // x 2 x 3) indices.
        return (width - 1) * (height - 1) * 2 * 3
    }

    /**
     * Create an index buffer object for the vertices to wrap them together into
     * triangles, creating indices based on the width and height of the
     * heightmap.
     */
    private fun createIndexData(): ShortArray {

        val indexData = ShortArray(numElements)
        var offset = 0

        for (row in 0 until height - 1) {
            for (col in 0 until width - 1) {
                // Note: The (short) cast will end up underflowing the number
                // into the negative range if it doesn't fit, which gives us the
                // right unsigned number for OpenGL due to two's complement.
                // This will work so long as the heightmap contains 65536 pixels
                // or less.
                val topLeftIndexNum = (row * width + col).toShort()
                val topRightIndexNum = (row * width + col + 1).toShort()
                val bottomLeftIndexNum = ((row + 1) * width + col).toShort()
                val bottomRightIndexNum = ((row + 1) * width + col + 1).toShort()

                // Write out two triangles.
                indexData[offset++] = topLeftIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = topRightIndexNum

                indexData[offset++] = topRightIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = bottomRightIndexNum
            }
        }
        return indexData
    }

    fun bindData(heightmapProgram: HeightmapShaderProgram) {
        val attribPropertyList: List<AttributeProperty> = arrayListOf(
            AttributeProperty(
                VERTEX_POS_INDEX,
                POSITION_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_POS_OFFSET
            ),
            AttributeProperty(
                VERTEX_NORMAL_INDEX,
                NORMAL_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_NORMAL_OFFSET
            ),
            AttributeProperty(
                VERTEX_TEXTURE_INDEX,
                TEXTURE_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_TEXTURE_OFFSET
            )
        )
        vertexBuffer.bindData(attribPropertyList)
    }


    fun draw() {
        vertexBuffer.draw()
    }

    companion object {

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_NORMAL_INDEX = 1
        internal const val VERTEX_TEXTURE_INDEX = 2

        // 顶点坐标的每个属性的Size
        internal const val POSITION_COMPONENT_COUNT = 3   // x,y,z
        internal const val NORMAL_COMPONENT_COUNT = 3     // x,y,z
        internal const val TEXTURE_COMPONENT_COUNT = 2    // s,t

        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_NORMAL_OFFSET = POSITION_COMPONENT_COUNT * Float.SIZE_BYTES
        internal const val VERTEX_TEXTURE_OFFSET =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * Float.SIZE_BYTES

        // 每个顶点的属性组的Size
        internal const val VERTEX_TOTAL_COMPONENT_COUNT =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT)

        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_TOTAL_COMPONENT_COUNT * Float.SIZE_BYTES
    }


}
