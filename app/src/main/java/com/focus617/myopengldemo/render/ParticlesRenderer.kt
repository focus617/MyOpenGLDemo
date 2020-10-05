package com.focus617.myopengldemo.render

import android.content.Context
import android.graphics.Color
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.other.Cube
import com.focus617.myopengldemo.objects.particles.ParticleFireworksExplosion
import com.focus617.myopengldemo.objects.particles.ParticleShooter
import com.focus617.myopengldemo.objects.particles.ParticleSystem
import com.focus617.myopengldemo.objects.particles.Skybox
import com.focus617.myopengldemo.programs.other.ShapeShaderProgram
import com.focus617.myopengldemo.programs.particles.ParticleShaderProgram
import com.focus617.myopengldemo.programs.particles.SkyboxShaderProgram
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.MatrixHelper
import com.focus617.myopengldemo.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.properties.Delegates
import kotlin.random.Random

class ParticlesRenderer(val context: Context) : GLSurfaceView.Renderer {

    private val mModelMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)

    private val mViewProjectionMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    // Maximum saturation and value.
    private val hsv = floatArrayOf(0f, 1f, 1f)

    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
    private lateinit var redParticleShooter: ParticleShooter
    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var blueParticleShooter: ParticleShooter
    private lateinit var particleFireworksExplosion: ParticleFireworksExplosion

    private lateinit var cubeProgram: ShapeShaderProgram
    private lateinit var cube: Cube

    private lateinit var skyBoxProgram: SkyboxShaderProgram
    private lateinit var skyBox: Skybox

    private var random = Random
    private var globalStartTime by Delegates.notNull<Long>()

    private var particleTexture = 0
    private var skyboxTexture = 0

    private var xRotation: Float = 0f
    private var yRotation: Float = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        skyBoxProgram = SkyboxShaderProgram(context)
        skyBox = Skybox()

        cubeProgram = ShapeShaderProgram(context)
        cube = Cube()

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

        particleFireworksExplosion = ParticleFireworksExplosion()

        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture)

        skyboxTexture = TextureHelper.loadCubeMap(
            context, intArrayOf(
                R.drawable.left, R.drawable.right,
                R.drawable.bottom, R.drawable.top,
                R.drawable.front, R.drawable.back
            )
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
//        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect,
//            -1f, 1f, 1f, 10f)
//
//        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
//        Matrix.setLookAtM(
//            mViewMatrix, 0, 0f, -0.5f, -2.5f,
//            0f, 0f, 0f, 0f, 1.0f, 1.0f
//        )
//
//        // 视图转换：Multiply the view and projection matrices together
//        Matrix.multiplyMM(
//            viewProjectionMatrix, 0,
//            mProjectionMatrix, 0,
//            mViewMatrix, 0
//        )
//    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        MatrixHelper.perspectiveM(
            mProjectionMatrix, 45f, width.toFloat()
                    / height.toFloat(), 1f, 10f
        )

    }

    override fun onDrawFrame(gl: GL10?) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT)
        drawSkyBox()
        drawParticles()
        drawCube()
    }

    private fun drawSkyBox() {
        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.rotateM(mViewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(mViewMatrix, 0, -xRotation, 0f, 1f, 0f)
        Matrix.multiplyMM(
            mViewProjectionMatrix, 0,
            mProjectionMatrix, 0,
            mViewMatrix, 0
        )

        skyBoxProgram.useProgram()
        skyBoxProgram.setUniforms(mViewProjectionMatrix, skyboxTexture)
        skyBox.bindDataES3(skyBoxProgram)
        skyBox.drawES3()
    }

    private fun drawCube() {

        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.rotateM(mViewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(mViewMatrix, 0, -xRotation, 0f, 1f, 0f)
        Matrix.translateM(mViewMatrix, 0, 0f, -1.5f, -3f)
        Matrix.multiplyMM(
            mViewProjectionMatrix, 0,
            mProjectionMatrix, 0,
            mViewMatrix, 0
        )
        positionObjectInScene(-0.5f, -0.5f, 0.5f)
        cubeProgram.useProgram()
        cubeProgram.setUniforms(mMVPMatrix, skyboxTexture)
        cube.bindDataES3()
        cube.drawES3()
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(mModelMatrix, 0)

        Matrix.translateM(mModelMatrix, 0, x, y, z)

        // 视图转换：计算模型视图投影矩阵MVPMatrix，该矩阵可以将模型空间的坐标转换为归一化设备空间坐标
        Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0)
    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter.addParticles(particleSystem, currentTime, 2)
        greenParticleShooter.addParticles(particleSystem, currentTime, 2)
        blueParticleShooter.addParticles(particleSystem, currentTime, 2)


        if (random.nextFloat() < 0.02f) {
            hsv[0] = random.nextInt(360).toFloat()

            particleFireworksExplosion.addExplosion(
                particleSystem,
                Point(
                    -1f + random.nextFloat() * 2f,
                    2.6f + random.nextFloat() / 2f,
                    -1f + random.nextFloat() * 2f
                ),
                Color.HSVToColor(hsv),
                globalStartTime
            )
        }

        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.rotateM(mViewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(mViewMatrix, 0, -xRotation, 0f, 1f, 0f)
        Matrix.translateM(mViewMatrix, 0, 0f, -1.5f, -3f)
        Matrix.multiplyMM(
            mViewProjectionMatrix, 0,
            mProjectionMatrix, 0,
            mViewMatrix, 0
        )

        // Enable additive blending
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleProgram.useProgram()
        particleProgram.setUniforms(mViewProjectionMatrix, currentTime, particleTexture)
        particleSystem.bindDataES2(particleProgram)
        particleSystem.drawES2()

        glDisable(GL_BLEND)
    }

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 16f
        yRotation += deltaY / 16f

        if (yRotation < -90) {
            yRotation = -90f
        } else if (yRotation > 90) {
            yRotation = 90f
        }
    }

}