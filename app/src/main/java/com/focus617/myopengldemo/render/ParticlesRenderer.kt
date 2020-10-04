package com.focus617.myopengldemo.render

import android.content.Context
import android.graphics.Color
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.objects.particles.ParticleShooter
import com.focus617.myopengldemo.objects.particles.ParticleSystem
import com.focus617.myopengldemo.programs.particles.ParticleShaderProgram
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.MatrixHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.properties.Delegates

class ParticlesRenderer(val context: Context) : GLSurfaceView.Renderer {

    private val mModelMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)

    private val viewProjectionMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    /*
    // Maximum saturation and value.
    private final float[] hsv = {0f, 1f, 1f};*/

    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
    private lateinit var redParticleShooter: ParticleShooter
    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var blueParticleShooter: ParticleShooter

    private var globalStartTime by Delegates.notNull<Long>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // Enable additive blending
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        val particleDirection = Vector(0f, 0.5f, 0f)

        val angleVarianceInDegrees = 5f
        val speedVariance = 1f

        redParticleShooter = ParticleShooter(
            Point(-1f, 0f, 0f),
            particleDirection,
            Color.rgb(255, 50, 5),
            angleVarianceInDegrees,
            speedVariance
        )

        greenParticleShooter = ParticleShooter(
            Point(0f, 0f, 0f),
            particleDirection,
            Color.rgb(25, 255, 25),
            angleVarianceInDegrees,
            speedVariance
        )

        blueParticleShooter = ParticleShooter(
            Point(1f, 0f, 0f),
            particleDirection,
            Color.rgb(5, 50, 255),
            angleVarianceInDegrees,
            speedVariance
        )
    }

//    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//
//        // 设置渲染的OpenGL场景（视口）的位置和大小
//        Timber.d("width = $width, height = $height")
//
//        // Set the OpenGL viewport to fill the entire surface.
//        glViewport(0, 0, width, height)
//
//        // 计算透视投影矩阵 (Project Matrix)，而后将应用于onDrawFrame（）方法中的对象坐标
//        val aspect: Float = width.toFloat() / height.toFloat()
//        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f)
//
//        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
//        Matrix.setLookAtM(
//            mViewMatrix, 0, 0f, -2.5f, 5f,
//            0f, 0f, 0f, 0f, 1.0f, 0.0f
//        )
//
//        // 视图转换：Multiply the view and projection matrices together
//        Matrix.multiplyMM(
//            viewProjectionMatrix, 0,
//            mProjectionMatrix, 0, mViewMatrix, 0
//        )
//    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        MatrixHelper.perspectiveM(
            mProjectionMatrix, 45f, width.toFloat()
                    / height.toFloat(), 1f, 10f
        )
        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.translateM(mViewMatrix, 0, 0f, -1.5f, -5f)
        Matrix.multiplyMM(
            viewProjectionMatrix, 0, mProjectionMatrix, 0,
            mViewMatrix, 0
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT)

        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter.addParticles(particleSystem, currentTime, 5)
        greenParticleShooter.addParticles(particleSystem, currentTime, 5)
        blueParticleShooter.addParticles(particleSystem, currentTime, 5)

        particleProgram.useProgram()

        particleProgram.setUniforms(viewProjectionMatrix, currentTime)
        particleSystem.bindDataES2(particleProgram)
        particleSystem.drawES2()
    }

}