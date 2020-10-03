package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


open class XGLRenderer(open val context: Context) : GLSurfaceView.Renderer {

    protected val mModelMatrix = FloatArray(16)
    protected val mViewMatrix = FloatArray(16)
    protected val mProjectionMatrix = FloatArray(16)

    protected val mViewProjectionMatrix = FloatArray(16)
    protected val mMVPMatrix = FloatArray(16)

    private var mTriangle: Triangle? = null
    private var mSquare: Square? = null
    private var mCube: Cube? = null
    private var mAirHockey: AirHockeyV1? = null

    // 处理旋转
    protected open fun setupRotation() {
        // 进行旋转变换
        Matrix.rotateM(mViewMatrix, 0, getAngle(), 1.0f, 0f, 0f)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {

        // 设置渲染的OpenGL场景（视口）的位置和大小
        Timber.d("width = $width, height = $height")

        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        // 计算透视投影矩阵 (Project Matrix)，而后将应用于onDrawFrame（）方法中的对象坐标
        val aspect: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f)

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 2.0f, 3.0f,
            0f, 0f, 0f, 0f, 1.0f, 0.0f)

    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT)

        // 处理旋转
        setupRotation()

        // 视图转换：Multiply the view and projection matrices together
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // 放置物体
        positionObjectInScene(0f,0f,0f)

        onDrawShape()
    }

    fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(mModelMatrix, 0)

        Matrix.translateM(mModelMatrix, 0, x, y, z)

        // 视图转换：计算模型视图投影矩阵MVPMatrix，该矩阵可以将模型空间的坐标转换为归一化设备空间坐标
        Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0)
    }

    private fun onDrawShape() {
        when (shape) {
            Shape.Triangle -> {
                // 绘制三角形
                if (mTriangle == null) mTriangle = Triangle()
                mTriangle!!.draw(mMVPMatrix)
            }
            Shape.Square -> {
                // 绘制正方形
                if (mSquare == null) mSquare = Square()
                mSquare!!.draw(mMVPMatrix)
            }
            Shape.Cube -> {
                // 绘制正方形
                if (mCube == null) mCube = Cube()
                mCube!!.draw(mMVPMatrix)
            }
            Shape.AirHockey, Shape.Unknown -> {
                // 绘制正方形
                if (mAirHockey == null) mAirHockey = AirHockeyV1(context)
                mAirHockey!!.draw(mMVPMatrix)
            }
            //Shape.Unknown -> return
        }
    }

    private var shape: Shape = Shape.Unknown
    fun setupShape(shape: Shape) {
        this.shape = shape
    }


    /**
     * 在 SurfaceView中通过触摸事件获取到要视图矩阵旋转的角度
     * 由于渲染器代码在与应用程序的主用户界面线程在不同的线程上运行，因此必须将此公共变量声明为volatile。
     */
    @Volatile
    var mAngle = 0f

    fun getAngle(): Float {
        return mAngle
    }

    fun setAngle(angle: Float) {
        mAngle = angle
    }


    companion object {

        enum class Shape {
            Unknown,
            Triangle,
            Square,
            Cube,
            AirHockey
        }
    }

}