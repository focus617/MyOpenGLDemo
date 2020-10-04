package com.focus617.myopengldemo.objects.particles

import android.graphics.Color
import android.opengl.GLES20
import com.focus617.myopengldemo.data.VertexArrayES2
import com.focus617.myopengldemo.data.VertexArrayES3
import com.focus617.myopengldemo.programs.particles.ParticleShaderProgram
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import timber.log.Timber

class ParticleSystem(private val maxParticleCount: Int) {
    private val particles: FloatArray = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)

    private val vertexArray = VertexArrayES2(particles)

    private var currentParticleCount = 0
    private var nextParticle = 0

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

    fun bindDataES2(particleProgram: ParticleShaderProgram) {
        var dataOffset = 0
        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, STRIDE
        )

        dataOffset += POSITION_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT, STRIDE
        )

        dataOffset += COLOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getDirectionVectorAttributeLocation(),
            VECTOR_COMPONENT_COUNT, STRIDE
        )

        dataOffset += VECTOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getParticleStartTimeAttributeLocation(),
            PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE
        )
    }

    fun drawES2() {
        Timber.d("drawES2(): currentParticleCount = $currentParticleCount")

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 3
        private const val VECTOR_COMPONENT_COUNT = 3
        private const val PARTICLE_START_TIME_COMPONENT_COUNT = 1

        private const val TOTAL_COMPONENT_COUNT = (
                POSITION_COMPONENT_COUNT
                + COLOR_COMPONENT_COUNT
                + VECTOR_COMPONENT_COUNT
                + PARTICLE_START_TIME_COMPONENT_COUNT)

        private const val STRIDE: Int = TOTAL_COMPONENT_COUNT * Float.SIZE_BYTES
    }
}