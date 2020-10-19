package com.focus617.myopengldemo.renderers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.geometry.d3.Cube
import com.focus617.myopengldemo.objects.particles.*
import com.focus617.myopengldemo.programs.particles.SkeyCubeShaderProgram
import com.focus617.myopengldemo.programs.particles.HeightmapShaderProgram
import com.focus617.myopengldemo.programs.particles.ParticleShaderProgram
import com.focus617.myopengldemo.programs.particles.SkyboxShaderProgram
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import com.focus617.myopengldemo.util.Geometry.Point
import com.focus617.myopengldemo.util.MatrixHelper
import com.focus617.myopengldemo.util.TextureHelper
import com.focus617.myopengldemo.util.TextureHelper.FilterMode
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.properties.Delegates
import kotlin.random.Random

class ParticlesRenderer(val context: Context) : GLSurfaceView.Renderer {

    private val mModelMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mViewMatrixForSkybox = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)

    private val mMVPMatrix = FloatArray(16)

    private val mViewProjectionMatrix = FloatArray(16)
    private val mModelViewMatrix = FloatArray(16)

    private val tempMatrix = FloatArray(16)
    private val it_modelViewMatrix = FloatArray(16)

    // Maximum saturation and value.
    private val hsv = floatArrayOf(0f, 1f, 1f)

    private val pointLightPositions = floatArrayOf(
        -1f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f
    )

    private val pointLightColors = floatArrayOf(
        1.00f, 0.20f, 0.02f,
        0.02f, 0.25f, 0.02f,
        0.02f, 0.20f, 1.00f
    )


    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
    private lateinit var redParticleShooter: ParticleShooter
    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var blueParticleShooter: ParticleShooter
    private lateinit var particleFireworksExplosion: ParticleFireworksExplosion

    private lateinit var cubeProgram: SkeyCubeShaderProgram
    private lateinit var cube: Cube

    private lateinit var skyBoxProgram: SkyboxShaderProgram
    private lateinit var skyBox: Skybox

    private lateinit var heightmapProgram: HeightmapShaderProgram
    private lateinit var heightmap: Heightmap

    //private lateinit var vectorToLight: Vector
    private val vectorToLight = floatArrayOf(0.61f, 0.64f, -0.47f, 0f)

    private var random = Random
    private var globalStartTime by Delegates.notNull<Long>()

    private var particleTexture = 0
    private var skyboxTexture = 0
    private var grassTexture = 0
    private var stoneTexture = 0

    private var xRotation: Float = 0f
    private var yRotation: Float = 0f

    companion object {
        internal const val MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FF
        internal const val isNight: Boolean = false
    }

    private val maxAnisotropy = FloatArray(1)
    private var supportsAnisotropicFiltering = false
    fun supportsAnisotropicFiltering(): Boolean {
        return supportsAnisotropicFiltering
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

        val extensions = glGetString(GL_EXTENSIONS)
        supportsAnisotropicFiltering = extensions.contains("GL_EXT_texture_filter_anisotropic")
        Timber.v("Anisotropic filtering supported = $supportsAnisotropicFiltering")

        if (supportsAnisotropicFiltering) {
            glGetFloatv(MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy, 0)
            Timber.v("Maximum anisotropy = ${maxAnisotropy[0]}")
        }


        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        heightmapProgram = HeightmapShaderProgram(context)
        heightmap = Heightmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.heightmap, null)
        )

        skyBoxProgram = SkyboxShaderProgram(context)
        skyBox = Skybox()

        cubeProgram = SkeyCubeShaderProgram(context)
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
        grassTexture = TextureHelper.loadTexture(context, R.drawable.noisy_grass_public_domain)
        stoneTexture = TextureHelper.loadTexture(context, R.drawable.stone_public_domain)

        if (isNight) {
            Timber.d("onSurfaceCreated(): Enter night mode.")
            skyboxTexture = TextureHelper.loadCubeMap(
                context,
                intArrayOf(
                    R.drawable.night_left, R.drawable.night_right,
                    R.drawable.night_bottom, R.drawable.night_top,
                    R.drawable.night_front, R.drawable.night_back
                )
            )
            //vectorToLight = Vector(0.30f, 0.35f, -0.89f).normalize()

        } else {
            Timber.d("onSurfaceCreated(): Enter daytime mode.")
            skyboxTexture = TextureHelper.loadCubeMap(
                context,
                intArrayOf(
                    R.drawable.left, R.drawable.right,
                    R.drawable.bottom, R.drawable.top,
                    R.drawable.front, R.drawable.back
                )
            )
            //vectorToLight = Vector(0.61f, 0.64f, -0.47f).normalize()
        }
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
        // 设置渲染的OpenGL场景（视口）的位置和大小
        Timber.d("width = $width, height = $height")

        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        // 计算透视投影矩阵 (Project Matrix)
        val aspect: Float = width.toFloat() / height.toFloat()
        MatrixHelper.perspectiveM(mProjectionMatrix, 45f, aspect, 1f, 100f)

        updateViewMatrices()

    }

    override fun onDrawFrame(gl: GL10?) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        drawHeightmap()
        drawSkyBox()
        drawParticles()
        drawCube()
    }

    private fun drawHeightmap() {
        Matrix.setIdentityM(mModelMatrix, 0)
        // Expand the heightmap's dimensions, but don't expand the height as
        // much so that we don't get insanely tall mountains.
        Matrix.scaleM(mModelMatrix, 0, 100f, 10f, 100f)
        updateMvpMatrix()

        heightmapProgram.use()

        // Put the light positions into eye space.
        /*
        heightmapProgram.setUniforms(mMVPMatrix, vectorToLight)
         */
        val vectorToLightInEyeSpace = FloatArray(4)
        val pointPositionsInEyeSpace = FloatArray(12)
        Matrix.multiplyMV(vectorToLightInEyeSpace, 0, mViewMatrix, 0, vectorToLight, 0)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 0, mViewMatrix, 0, pointLightPositions, 0)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 4, mViewMatrix, 0, pointLightPositions, 4)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 8, mViewMatrix, 0, pointLightPositions, 8)

        heightmapProgram.setUniforms(
            mModelViewMatrix, it_modelViewMatrix, mMVPMatrix,
            vectorToLightInEyeSpace, pointPositionsInEyeSpace,
            pointLightColors,
            grassTexture, stoneTexture
        )

        heightmap.bindData(heightmapProgram)
        heightmap.draw()
    }

    private fun drawSkyBox() {
        Matrix.setIdentityM(mModelMatrix, 0)
        updateMvpMatrixForSkybox()

        glDepthFunc(GL_LEQUAL) // This avoids problems with the skybox itself getting clipped.

        skyBoxProgram.use()
        skyBoxProgram.setUniforms(mMVPMatrix, skyboxTexture)
        skyBox.bindDataES3(skyBoxProgram)
        skyBox.drawES3()

        glDepthFunc(GL_LESS)
    }

    private fun drawCube() {
        positionObjectInScene(8f, 2f, 7f)
        updateViewMatrices()

        cubeProgram.use()
        cubeProgram.setUniforms(
            mModelMatrix, mViewMatrix, mProjectionMatrix, skyboxTexture)
        cube.bindData()
        cube.draw()
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

        Matrix.setIdentityM(mModelMatrix, 0)
        updateMvpMatrix()

        // Enable additive blending
        glDepthMask(false)
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleProgram.use()
        particleProgram.setUniforms(mMVPMatrix, currentTime, particleTexture)
        particleSystem.bindDataES3(particleProgram)
        particleSystem.drawES3()

        glDisable(GL_BLEND)
        glDepthMask(true)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.rotateM(mModelMatrix, 0, -45f, 1f, 0f, 1f)
        Matrix.translateM(mModelMatrix, 0, x, y, z)
    }

    private fun updateViewMatrices() {
        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.rotateM(mViewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(mViewMatrix, 0, -xRotation, 0f, 1f, 0f)
        System.arraycopy(mViewMatrix, 0, mViewMatrixForSkybox, 0, mViewMatrix.size)

        // We want the translation to apply to the regular view matrix, and not the skybox.
        Matrix.translateM(mViewMatrix, 0, 0f, -1.5f, -5f)
    }

    private fun updateMvpMatrix() {
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.invertM(tempMatrix, 0, mModelViewMatrix, 0)
        Matrix.transposeM(it_modelViewMatrix, 0, tempMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0,
            mModelViewMatrix, 0)
    }

    private fun updateMvpMatrixForSkybox() {
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrixForSkybox, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0,
            mModelViewMatrix, 0)
    }

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 16f
        yRotation += deltaY / 16f

        if (yRotation < -90) {
            yRotation = -90f
        } else if (yRotation > 90) {
            yRotation = 90f
        }

        // Setup view matrix
        updateViewMatrices()
    }

    fun handleFilterModeChange(filterMode: FilterMode) {
        TextureHelper.adjustTextureFilters(
            grassTexture,
            filterMode,
            supportsAnisotropicFiltering,
            maxAnisotropy[0]
        )
        TextureHelper.adjustTextureFilters(
            stoneTexture,
            filterMode,
            supportsAnisotropicFiltering,
            maxAnisotropy[0]
        )
    }


}