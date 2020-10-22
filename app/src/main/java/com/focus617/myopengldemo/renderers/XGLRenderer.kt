package com.focus617.myopengldemo.renderers

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.base.Model
import com.focus617.myopengldemo.base.basic.Camera
import com.focus617.myopengldemo.base.basic.PointLight
import com.focus617.myopengldemo.base.objectbuilder.IndexMeshObject
import com.focus617.myopengldemo.objects.geometry.d2.Circle
import com.focus617.myopengldemo.objects.geometry.d2.Square
import com.focus617.myopengldemo.objects.geometry.d2.Star
import com.focus617.myopengldemo.objects.geometry.d2.Triangle
import com.focus617.myopengldemo.objects.geometry.d3.ball.Earth
import com.focus617.myopengldemo.objects.geometry.d3.ball.Moon
import com.focus617.myopengldemo.objects.geometry.d3.ball.Sun
import com.focus617.myopengldemo.objects.geometry.d3.cube.Cube
import com.focus617.myopengldemo.util.Vector
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

    private lateinit var mCube: Cube
    private val mCubePos: Vector = Vector(0.0f, 0.0f, 0.0f)
    private var boxTexture = 0

    private lateinit var mSun: Sun

    private lateinit var mEarth: Earth
    private var earthDayTexture = 0
    private var earthNightTexture = 0

    private lateinit var mMoon: Moon
    private var moonTexture = 0

    var eAngle = 0f //地球自转角度
    var cAngle = 0f //月球自转的角度

    private lateinit var mModel: Model

    private lateinit var mTriangle: Triangle
    private lateinit var mSquare: Square
    private lateinit var mStar: Star

    private lateinit var mObject: IndexMeshObject


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {


        // 设置重绘背景框架颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        //打开深度检测
        glEnable(GL_DEPTH_TEST)

        //打开背面剪裁
        glEnable(GL_CULL_FACE)

        mSun = Sun(context, 1.0f)

//        mEarth = Earth(context, 1.0f)
//        earthDayTexture =TextureHelper.loadTexture(context, R.drawable.earth)
//        earthNightTexture =TextureHelper.loadTexture(context, R.drawable.earthn)

//        mMoon = Moon(context, 0.5f)
//        moonTexture =TextureHelper.loadTexture(context, R.drawable.moon)

//        mCube = Cube(context)
//        boxTexture = TextureHelper.loadTexture(context, R.drawable.box)

//        mTriangle = Triangle(context)
//        mSquare = Square(context)
//        mStar = Star(context, 0.4f, 1.0f, -0.3f)

//        mObject = Circle(context, 1f)


//        // build model
        mModel = Model()
//        mModel.load(context, "3dModel/statueOfPharaoh/sculpt.obj")
        mModel.load(context, "3dModel/teapot/teapot_t.obj")

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
        MatrixHelper.perspectiveM(mProjectionMatrix, yFovInDegrees, aspect, 0.1f, 500f)


        //启动一个线程定时旋转地球、月球
        object : Thread() {
            override fun run() {
                while (true) {
                    //地球自转角度
                    eAngle = (eAngle + 2) % 360
                    //天球自转角度
                    cAngle = (cAngle + 0.2f) % 360
                    try {
                        sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        placeCamera()

        drawSun()
//
////        drawEarth()
//
//        drawMoon()
//
//        drawStar()
//
//        drawCube()
//        drawTriangle()
//        drawSquare()
//
//        draw()

        drawModel()
    }

    private fun drawModel() {
        for ((key, mesh) in mModel.meshes) {
            mesh.positionObjectInScene()
            mesh.updateShaderUniforms(
                mesh.mModelMatrix, mViewMatrix, mProjectionMatrix,
                Camera.Position)
            mesh.draw()
        }
    }

    private fun drawSun() {
        mSun.positionObjectInScene()
        mSun.moveTo(PointLight.position)
        mSun.updateShaderUniforms(mSun.mModelMatrix, mViewMatrix, mProjectionMatrix)
        mSun.draw()
    }

    private fun drawEarth() {
        mEarth.positionObjectInScene()
        //地球自转
        mEarth.rotate(eAngle, 0f, 1f, 0f)

        mEarth.updateShaderUniforms(
            mEarth.mModelMatrix, mViewMatrix, mProjectionMatrix,
            Camera.Position, earthDayTexture, earthNightTexture
        )
        mEarth.draw()
    }

    private fun drawMoon() {
        mMoon.positionObjectInScene()
        mMoon.moveTo(Vector(-2.0f, 0.0f, -3.0f))
        //地球自转
        mMoon.rotate(eAngle, 0f, 1f, 0f)

        mMoon.updateShaderUniforms(
            mMoon.mModelMatrix, mViewMatrix, mProjectionMatrix,
            Camera.Position, moonTexture
        )
        mMoon.draw()
    }

    private fun drawCube() {
        mCube.positionObjectInScene()
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

    private fun draw() {
        mObject.positionObjectInScene()

        (mObject as Circle).updateShaderUniforms(
            mObject.mModelMatrix, mViewMatrix, mProjectionMatrix,
            Camera.Position, earthDayTexture, earthNightTexture
        )

        mObject.drawLine()
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