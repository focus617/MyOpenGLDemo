package com.focus617.myopengldemo.data

import android.opengl.GLES31
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class VertexArrayEs3(vertexData: FloatArray, indexData: ShortArray?=null) {

    var mVAOId: IntBuffer = IntBuffer.allocate(1)   // 顶点数组对象
    var mVBOIds: IntBuffer = IntBuffer.allocate(2)  // 顶点缓存对象
    var withElement: Boolean = false

    init {
        // 创建缓存，并绑定缓存类型
        // mVBOIds[O] - used to store vertex attribute data
        // mVBOIds[l] - used to store element indices
        // allocate only on the first draw
        GLES31.glGenBuffers(2, mVBOIds)
        Timber.d("Create VBO, ID: $mVBOIds")

        setupVertices(vertexData)
        if(indexData!=null){
            setupElements(indexData)
            withElement = true
        }
    }

    private fun setupVertices(vertices: FloatArray) {

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, mVBOIds.get(0))
        // 把定义的顶点数据复制到缓存中
        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            vertices.size * Float.SIZE_BYTES,
            FloatBuffer.wrap(vertices),
            GLES31.GL_STATIC_DRAW
        )
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
    }

    private fun setupElements(indices: ShortArray) {
        // bind buffer object for element indices
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, mVBOIds.get(1))
        GLES31.glBufferData(
            GLES31.GL_ELEMENT_ARRAY_BUFFER,
            indices.size * Short.SIZE_BYTES,
            ShortBuffer.wrap(indices),
            GLES31.GL_STATIC_DRAW
        )
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}
