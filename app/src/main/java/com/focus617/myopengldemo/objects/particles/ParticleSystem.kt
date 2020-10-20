package com.focus617.myopengldemo.objects.particles

import android.graphics.Color
import android.opengl.GLES31.*
import com.focus617.myopengldemo.base.objectbuilder.AttributeProperty
import com.focus617.myopengldemo.base.objectbuilder.VertexArray
import com.focus617.myopengldemo.base.objectbuilder.VertexBuffer
import com.focus617.myopengldemo.programs.particles.ParticleShaderProgram
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import timber.log.Timber

class ParticleSystem(private val maxParticleCount: Int) {

    private val particles: FloatArray = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)

    private var currentParticleCount = 0
    private var nextParticle = 0

    private val vertexArray = VertexArray(particles)
    private val vertexBuffer = VertexBuffer.build(vertexArray, currentParticleCount)



    fun addParticle(position: Point, color: Int, direction: Vector, particleStartTime: Float) {

        val particleOffset = nextParticle * TOTAL_COMPONENT_COUNT
        nextParticle++
        if (nextParticle == maxParticleCount) {
            // Start over at the beginning, but keep currentParticleCount so
            // that all the other particles still get drawn.
            nextParticle = 0
        }

        var currentOffset = particleOffset

        particles[currentOffset++] = position.x
        particles[currentOffset++] = position.y
        particles[currentOffset++] = position.z

        particles[currentOffset++] = Color.red(color) / 255f
        particles[currentOffset++] = Color.green(color) / 255f
        particles[currentOffset++] = Color.blue(color) / 255f

        particles[currentOffset++] = direction.x
        particles[currentOffset++] = direction.y
        particles[currentOffset++] = direction.z

        particles[currentOffset++] = particleStartTime

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT)

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++
        }
    }

    fun bindDataES3(particleProgram: ParticleShaderProgram) {

        val attribPropertyList: List<AttributeProperty> = arrayListOf(
            // 顶点的位置属性
            AttributeProperty(
                VERTEX_POS_INDEX,
                POSITION_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_POS_OFFSET
            ),

            // 顶点的颜色属性
            AttributeProperty(
                VERTEX_COLOR_INDEX,
                COLOR_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_COLOR_OFFSET
            ),

            AttributeProperty(
                VERTEX_VECTOR_INDEX,
                VECTOR_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_VECTOR_OFFSET
            ),

            // 顶点的颜色属性
            AttributeProperty(
                VERTEX_PARTICLE_START_TIME_INDEX,
                PARTICLE_START_TIME_COMPONENT_COUNT,
                VERTEX_STRIDE,
                VERTEX_PARTICLE_START_TIME_OFFSET
            )
        )
        vertexBuffer.bindData(attribPropertyList)
    }

    fun drawES3() {
        Timber.d("drawES3(): currentParticleCount = $currentParticleCount")

        vertexBuffer.updateVertices(vertexArray, currentParticleCount)

        // Bind the VAO and then draw with VAO settings
        glBindVertexArray(vertexBuffer.mVaoId)

        glDrawArrays(GL_POINTS, 0, currentParticleCount)

        // Reset to the default VAO
        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }

    companion object {
        // 顶点坐标的每个属性的Index
        internal const val VERTEX_POS_INDEX = 0
        internal const val VERTEX_COLOR_INDEX = 1
        internal const val VERTEX_VECTOR_INDEX = 2
        internal const val VERTEX_PARTICLE_START_TIME_INDEX = 3

        internal const val POSITION_COMPONENT_COUNT = 3  //x,y,z
        internal const val COLOR_COMPONENT_COUNT = 3     //r,g,b
        internal const val VECTOR_COMPONENT_COUNT = 3
        internal const val PARTICLE_START_TIME_COMPONENT_COUNT = 1

        internal const val VERTEX_POS_OFFSET = 0
        internal const val VERTEX_COLOR_OFFSET = POSITION_COMPONENT_COUNT * Float.SIZE_BYTES
        internal const val VERTEX_VECTOR_OFFSET =
            (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT) * Float.SIZE_BYTES
        internal const val VERTEX_PARTICLE_START_TIME_OFFSET =
            (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT+VECTOR_COMPONENT_COUNT) * Float.SIZE_BYTES

        internal const val TOTAL_COMPONENT_COUNT = (
                POSITION_COMPONENT_COUNT
                + COLOR_COMPONENT_COUNT
                + VECTOR_COMPONENT_COUNT
                + PARTICLE_START_TIME_COMPONENT_COUNT)

        internal const val VERTEX_STRIDE: Int = TOTAL_COMPONENT_COUNT * Float.SIZE_BYTES
    }

    //    fun bindDataES2(particleProgram: ParticleShaderProgram) {
//        var dataOffset = 0
//        vertexArray.setVertexAttribPointer(
//            dataOffset,
//            particleProgram.getPositionAttributeLocation(),
//            POSITION_COMPONENT_COUNT, STRIDE
//        )
//
//        dataOffset += POSITION_COMPONENT_COUNT
//        vertexArray.setVertexAttribPointer(
//            dataOffset,
//            particleProgram.getColorAttributeLocation(),
//            COLOR_COMPONENT_COUNT, STRIDE
//        )
//
//        dataOffset += COLOR_COMPONENT_COUNT
//        vertexArray.setVertexAttribPointer(
//            dataOffset,
//            particleProgram.getDirectionVectorAttributeLocation(),
//            VECTOR_COMPONENT_COUNT, STRIDE
//        )
//
//        dataOffset += VECTOR_COMPONENT_COUNT
//        vertexArray.setVertexAttribPointer(
//            dataOffset,
//            particleProgram.getParticleStartTimeAttributeLocation(),
//            PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE
//        )
//    }

//    fun drawES2() {
//        Timber.d("drawES2(): currentParticleCount = $currentParticleCount")
//
//        glDrawArrays(GL_POINTS, 0, currentParticleCount)
//        glBindBuffer(GL_ARRAY_BUFFER, 0)
//    }
}