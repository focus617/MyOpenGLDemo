package com.focus617.myopengldemo.objects.particles

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES31
import android.opengl.GLES31.*
import com.focus617.myopengldemo.data.VertexBuffer
import com.focus617.myopengldemo.objects.other.Cube
import com.focus617.myopengldemo.programs.particles.HeightmapShaderProgram
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
        for(i in 0..11) Timber.d("VertexBuffer $i : ${vertices[i]}")

        Timber.d("Build VertexBuffer: indices(size=${indices.size})")
        for(i in 0..11) Timber.d("VertexBuffer $i : ${indices[i]}")

        vertexBuffer = VertexBuffer(vertices, indices)
    }
    
    
    /**
     * Copy the heightmap data into a vertex buffer object.
     */
    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        val heightmapVertices = FloatArray(width * height * POSITION_COMPONENT_COUNT)
        var offset = 0
        for (row in 0 until height) {
            for (col in 0 until width) {
                // The heightmap will lie flat on the XZ plane and centered
                // around (0, 0), with the bitmap width mapped to X and the
                // bitmap height mapped to Z, and Y representing the height. We
                // assume the heightmap is grayscale, and use the value of the
                // red color to determine the height.
                val xPosition = (col.toFloat() / (width - 1).toFloat()) - 0.5f
                val yPosition =
                    Color.red(pixels[row * height + col]).toFloat() / 255.toFloat()
                val zPosition = (row.toFloat() / (height - 1).toFloat()) - 0.5f

                heightmapVertices[offset++] = xPosition
                heightmapVertices[offset++] = yPosition
                heightmapVertices[offset++] = zPosition
            }
        }
        return heightmapVertices
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

    fun bindDataES2(heightmapProgram: HeightmapShaderProgram) {
        vertexBuffer.setVertexAttribPointer(
            0,
            heightmapProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            0
        )
    }
    fun drawES2() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBuffer.mVBOIds.get(0))
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun bindDataES3(heightmapProgram: HeightmapShaderProgram) {
        //Generate VAO ID
        glGenVertexArrays(vertexBuffer.mVAOId.capacity(), vertexBuffer.mVAOId)

        // Bind the VAO and then set up the vertex attributes
        glBindVertexArray(vertexBuffer.getVaoId())

        // 链接顶点属性，告诉OpenGL该如何解析顶点数据
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.mVBOIds.get(0))

        // 启用顶点数组
        glEnableVertexAttribArray(VERTEX_POS_INDEX)

        // 顶点的位置属性
        glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_STRIDE,
            VERTEX_POS_OFFSET
        )

        if (vertexBuffer.withElement) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBuffer.mVBOIds.get(1))
        }

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    fun drawES3() {
        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(vertexBuffer.getVaoId())

        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        // Reset to the default VAO
        glBindVertexArray(0)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3

        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        // 顶点坐标的每个属性的Size
        internal const val VERTEX_POS_SIZE = 3          //x,y,z
        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_ATTRIBUTE_SIZE = VERTEX_POS_SIZE
        // 连续的顶点属性组之间的间隔
        internal const val VERTEX_STRIDE = VERTEX_ATTRIBUTE_SIZE * Float.SIZE_BYTES
    }


}
