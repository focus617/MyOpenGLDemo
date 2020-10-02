package com.focus617.myopengldemo.render

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import timber.log.Timber
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


open class XGLRenderer(open val context: Context) : GLSurfaceView.Renderer {

    protected val mMVPMatrix = FloatArray(16)

    protected val mProjectionMatrix = FloatArray(16)

    protected val mViewMatrix = FloatArray(16)


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

    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        glClear(GL_COLOR_BUFFER_BIT)

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3.5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // 处理旋转
        setupRotation()

        // 视图转换：计算模型视图投影矩阵MVPMatrix，该矩阵可以将模型空间的坐标转换为归一化设备空间坐标
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        onDrawShape()
    }

    protected open fun onDrawShape() {
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

        // Create the program object
        fun buildProgram(vertexShaderCode: String, fragmentShaderCode: String):Int{
            // 顶点着色器
            val vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)

            // 片元着色器
            val fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

            // 把着色器链接为一个着色器程序对象
            var mProgramObject = glCreateProgram()
            glAttachShader(mProgramObject, vertexShader)
            glAttachShader(mProgramObject, fragmentShader)
            glLinkProgram(mProgramObject)

            val success: IntBuffer = IntBuffer.allocate(1)
            glGetProgramiv(mProgramObject, GL_LINK_STATUS, success)
            if (success.get(0) == 0) {
                Timber.e(glGetProgramInfoLog(mProgramObject))
                glDeleteProgram(mProgramObject)
                mProgramObject = 0
            } else {
                Timber.d("GLProgram $mProgramObject is ready.")
            }

            // 销毁不再需要的着色器对象
            glDeleteShader(vertexShader)
            glDeleteShader(fragmentShader)
            // 释放着色器编译器使用的资源
            glReleaseShaderCompiler()

            return mProgramObject
        }

        /**
         * 创建着色器：Create a shader object, load the shader source, and compile the shader
         * @Parameter [type]顶点着色器类型（GLES31.GL_VERTEX_SHADER）或片段着色器类型（GLES31.GL_FRAGMENT_SHADER）
         */
        private fun loadShader(type: Int, shaderCode: String): Int {

            // 创建一个着色器对象
            var shader = glCreateShader(type)
            if (shader == 0) return 0

            // 将源代码加载到着色器并进行编译
            glShaderSource(shader, shaderCode)
            glCompileShader(shader)

            // 检查编译状态
            val success: IntBuffer = IntBuffer.allocate(1)
            glGetShaderiv(shader, GL_COMPILE_STATUS, success)
            if (success.get(0) == 0) {
                Timber.e(glGetShaderInfoLog(shader))
                glDeleteShader(shader)
                shader = 0
            }

            return shader
        }
    }

}