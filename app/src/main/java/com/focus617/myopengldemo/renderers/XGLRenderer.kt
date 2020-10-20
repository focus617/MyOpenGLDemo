package com.focus617.myopengldemo.renderers

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.base.Model
import com.focus617.myopengldemo.base.basic.Camera
import com.focus617.myopengldemo.base.basic.PointLight
import com.focus617.myopengldemo.objects.geometry.d2.Star
import com.focus617.myopengldemo.objects.geometry.d2.Square
import com.focus617.myopengldemo.objects.geometry.d3.Cube
import com.focus617.myopengldemo.objects.geometry.d3.Cube2
import com.focus617.myopengldemo.objects.geometry.d2.Triangle
import com.focus617.myopengldemo.objects.geometry.d3.Ball
import com.focus617.myopengldemo.util.Geometry.Companion.Vector
import com.focus617.myopengldemo.util.MatrixHelper
import com.focus617.myopengldemo.util.TextureHelper
import com.focus617.myopengldemo.util.clamp
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


open class XGLRenderer(open val context: Context) : GLSurfaceView.Renderer {

    private val mCubeModelMatrix = FloatArray(16)
    private var mViewMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val it_modelViewMatrix = FloatArray(16)

    private lateinit var mCube: Cube2
    private val mCubePos: Vector = Vector(0.0f, 0.0f, 0.0f)
    private var boxTexture = 0

    private lateinit var mLightCube: Cube
    private lateinit var mBall: Ball

    private lateinit var mModel: Model

    private lateinit var mTriangle: Triangle
    private lateinit var mSquare: Square
    private lateinit var mStar: Star


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {


        // 设置重绘背景框架颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        //打开深度检测
        glEnable(GL_DEPTH_TEST)

        //打开背面剪裁
        glEnable(GL_CULL_FACE)

        mLightCube = Cube(context)
        mBall = Ball(context, 1.0f)

        mCube = Cube2(context)
        boxTexture = TextureHelper.loadTexture(context, R.drawable.box)

        mTriangle = Triangle(context)
        mSquare = Square(context)
        mStar = Star(context, 0.4f, 1.0f, -0.3f)


//        // build model
//        mModel = Model(context, "sculpt.obj")
//        // build shader program


    }

    private var yFovInDegrees: Float = 45f
    private var aspect: Float = 0f
    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {

        // 设置渲染的OpenGL场景（视口）的位置和大小
        Timber.d("width = $width, height = $height")

        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        // 计算透视投影矩阵 (Project Matrix)，而后将应用于onDrawFrame（）方法中的对象坐标
        aspect = width.toFloat() / height.toFloat()
//        Matrix.frustumM(
//            mProjectionMatrix, 0,
//            -aspect, aspect, -1f, 1f,
//            2f, 50f
//        )
        MatrixHelper.perspectiveM(mProjectionMatrix, yFovInDegrees, aspect, 0.1f, 100f)

    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        placeCamera()

        drawLightCube()
        drawCube()

        drawBall()
        drawTriangle()
        drawSquare()
        drawStar()
    }

    private fun drawLightCube() {
        mLightCube.moveTo(PointLight.position)
        mLightCube.updateShaderUniforms(mLightCube.mModelMatrix, mViewMatrix, mProjectionMatrix)
        mLightCube.draw()
    }

    private fun drawBall() {
        mBall.positionObjectInScene(-3.0f, 0f, 0f)
        mBall.updateShaderUniforms(mBall.mModelMatrix, mViewMatrix, mProjectionMatrix, Camera.Position)
        mBall.draw()
    }

    private fun drawCube() {
        mCube.moveTo(mCubePos)
//        updateItModelViewMatrix()

        mCube.updateShaderUniforms(
            mCube.mModelMatrix, mViewMatrix, mProjectionMatrix, it_modelViewMatrix,
            Camera.Position, boxTexture
        )
        mCube.draw()

    }

    private fun drawTriangle() {
        mTriangle.positionObjectInScene(-2.0f, 3.0f, -7.0f)
        mTriangle.updateShaderUniforms(mTriangle.mModelMatrix, mViewMatrix, mProjectionMatrix)
        mTriangle.draw()
    }

    private fun drawSquare() {
        mSquare.positionObjectInScene(2.0f, -3.0f, -7.0f)
        mSquare.updateShaderUniforms(mSquare.mModelMatrix, mViewMatrix, mProjectionMatrix)
        mSquare.draw()
    }

    private fun drawStar() {
        mStar.positionObjectInScene(-2.0f, -3.0f, -7.0f)
        mStar.updateShaderUniforms(mStar.mModelMatrix, mViewMatrix, mProjectionMatrix)
        mStar.draw()
    }

    private fun updateItModelViewMatrix() {
        val mModelViewMatrix = FloatArray(16)
        val tempMatrix = FloatArray(16)

        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mCubeModelMatrix, 0)
        Matrix.invertM(tempMatrix, 0, mModelViewMatrix, 0)
        Matrix.transposeM(it_modelViewMatrix, 0, tempMatrix, 0)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(mCubeModelMatrix, 0)
        Matrix.translateM(mCubeModelMatrix, 0, x, y, z)
    }

    private fun positionObjectInScene(position: Vector) {
        positionObjectInScene(position.x, position.y, position.z)
    }


    /**
     * 在 SurfaceView中通过触摸事件控制相机的位置
     */
    private var xRotation: Float = 90f
    private var yRotation: Float = 0f

    private fun placeCamera() {

        Camera.rotate(yRotation, xRotation)
//        Timber.d("Camera at ${Camera.Position}")

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        mViewMatrix = Camera.lookAt()

    }

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        val sensitivity: Float = 480f

        xRotation += deltaX / sensitivity
        yRotation += deltaY / sensitivity

        yRotation = clamp(yRotation, -180f, 180f)

        // Setup view matrix
        placeCamera()
    }

    fun handleScroll(scale: Float) {
        if (yFovInDegrees in 1.0f..45.0f)
            yFovInDegrees *= scale

        yFovInDegrees = clamp(yFovInDegrees, 1.0f, 45.0f)

        MatrixHelper.perspectiveM(mProjectionMatrix, yFovInDegrees, aspect, 0.1f, 100f)
    }
}