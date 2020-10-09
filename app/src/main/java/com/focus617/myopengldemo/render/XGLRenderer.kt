package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.focus617.myopengldemo.R
import com.focus617.myopengldemo.objects.other.Cube
import com.focus617.myopengldemo.objects.other.Square
import com.focus617.myopengldemo.objects.other.Triangle
import com.focus617.myopengldemo.programs.other.ShapeShaderProgram
import com.focus617.myopengldemo.util.TextureHelper
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


open class XGLRenderer(open val context: Context) : GLSurfaceView.Renderer {

    protected val mModelMatrix = FloatArray(16)
    protected val mViewMatrix = FloatArray(16)
    protected val mProjectionMatrix = FloatArray(16)

    protected val mViewProjectionMatrix = FloatArray(16)
    private val mModelViewMatrix = FloatArray(16)

    protected val mMVPMatrix = FloatArray(16)

    private var mTriangle: Triangle? = null
    private var mSquare: Square? = null
    private var mCube: Cube? = null

    private lateinit var mCubeProgram: ShapeShaderProgram

    private var skyboxTexture = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {

        // 设置渲染的OpenGL场景（视口）的位置和大小
        Timber.d("width = $width, height = $height")

        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height)

        // 计算透视投影矩阵 (Project Matrix)，而后将应用于onDrawFrame（）方法中的对象坐标
        val aspect: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f)

        // Build view matrix
        updateViewMatrices()
    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT)

        // 视图转换：Multiply the view and projection matrices together
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        onDrawShape()
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(mModelMatrix, 0)

        Matrix.translateM(mModelMatrix, 0, x, y, z)

        // 视图转换：计算模型视图投影矩阵MVPMatrix，该矩阵可以将模型空间的坐标转换为归一化设备空间坐标
        Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0)
    }

    /**
     * 在 SurfaceView中通过触摸事件获取到要视图矩阵旋转的角度
     */
    private var xRotation: Float = 0f
    private var yRotation: Float = 0f

    private fun updateViewMatrices() {
        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.rotateM(mViewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(mViewMatrix, 0, -xRotation, 0f, 1f, 0f)

        // translate camera to view matrix
        Matrix.translateM(mViewMatrix, 0, 0f, 0f, -3f)
    }

    private fun updateMvpMatrix() {
        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0)
    }


//    @Volatile
//    var mAngle = 0f
//
//    fun getAngle(): Float {
//        return mAngle
//    }
//
//    fun setAngle(angle: Float) {
//        mAngle = angle
//    }

    // 处理旋转
    protected open fun setupRotation() {

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
            0f, 0f, 3.0f,
            0f, 0f, 0f,
            0f, 1.0f, 0.0f)

        // 进行旋转变换
        //Matrix.rotateM(mViewMatrix, 0, getAngle(), 1.0f, 0f, 0f)
    }



    private var shape: Shape = Shape.Unknown
    fun setupShape(shape: Shape) {
        this.shape = shape
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
                if (mCube == null) {
                    mCubeProgram = ShapeShaderProgram(context)
                    mCube = Cube()
                    skyboxTexture = TextureHelper.loadCubeMap(
                        context,
                        intArrayOf(
                            R.drawable.left, R.drawable.right,
                            R.drawable.bottom, R.drawable.top,
                            R.drawable.front, R.drawable.back
                        )
                    )
                }
                drawCube()
            }
            else -> return
        }
    }

    private fun drawCube() {
        Matrix.setIdentityM(mModelMatrix, 0)
        positionObjectInScene(0f, 0f, 0f)
        Matrix.rotateM(mModelMatrix, 0, 45f, 1f, 0f, 1f)
        updateMvpMatrix()

        mCubeProgram.useProgram()
        mCubeProgram.setUniforms(mMVPMatrix, skyboxTexture)
        mCube!!.bindData()
        mCube!!.draw()
    }

    companion object {

        enum class Shape {
            Unknown,
            Triangle,
            Square,
            Cube
        }
    }

}