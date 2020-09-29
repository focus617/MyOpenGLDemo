package com.focus617.myopengldemo.render

import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.widget.Toast
import timber.log.Timber
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class XGLRender : GLSurfaceView.Renderer {

    private lateinit var mTriangle: Triangle

    private val mMVPMatrix = FloatArray(16)

    private val mProjectionMatrix = FloatArray(16)

    private val mViewMatrix = FloatArray(16)

    private var drawingObject: DrawingObject? = null


    // 处理旋转
    private fun setupRotation() {
//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()

        // 进行旋转变换
        Matrix.rotateM(mViewMatrix, 0, getAngle(), 0f, 0f, 1.0f)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // 设置渲染的OpenGL场景（视口）的位置和大小
        Timber.d("width = $width, height = $height")
        GLES31.glViewport(0, 0, width, height);

        // 计算透视投影矩阵 (Project Matrix)，而后将应用于onDrawFrame（）方法中的对象坐标
        val aspect: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f);

    }

    override fun onDrawFrame(unused: GL10) {
        // 首先清理屏幕，重绘背景颜色
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)

        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // 处理旋转
        setupRotation()

        // 视图转换：计算模型视图投影矩阵MVPMatrix，该矩阵可以将模型空间的坐标转换为归一化设备空间坐标
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        when (shape) {
            Shape.Triangle -> {
                // 绘制三角形
                drawingObject = Triangle()
                (drawingObject as Triangle).draw(mMVPMatrix)
            }
            Shape.Square -> {
                // 绘制正方形
                drawingObject = Square()
                (drawingObject as Square).draw(mMVPMatrix)
            }
            Shape.Unknown -> return
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

    /**
     * 创建着色器：Create a shader object, load the shader source, and compile the shader
     * @Parameter [type]顶点着色器类型（GLES31.GL_VERTEX_SHADER）或片段着色器类型（GLES31.GL_FRAGMENT_SHADER）
     */
    companion object {
        enum class Shape {
            Unknown,
            Triangle,
            Square
        }

        fun loadShader(type: Int, shaderCode: String): Int {

            // 创建一个着色器对象
            var shader = GLES31.glCreateShader(type)
            if (shader == 0) return 0

            // 将源代码加载到着色器并进行编译
            GLES31.glShaderSource(shader, shaderCode)
            GLES31.glCompileShader(shader)

            // 检查编译状态
            var success: IntBuffer = IntBuffer.allocate(1)
            GLES31.glGetShaderiv(shader, GLES31.GL_COMPILE_STATUS, success)
            if (success.get(0) == 0) {
                Timber.e(GLES31.glGetShaderInfoLog(shader));
                GLES31.glDeleteShader(shader);
                shader = 0
            }

            return shader
        }
    }

}