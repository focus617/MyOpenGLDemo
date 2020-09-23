package com.focus617.myopengldemo.render

import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class XGLRender : GLSurfaceView.Renderer {

    private lateinit var mTriangle: Triangle

    private val mMVPMatrix = FloatArray(16)

    private val mProjectionMatrix = FloatArray(16)

    private val mViewMatrix = FloatArray(16)

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
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);
        
        // 设置相机的位置，进而计算出视图矩阵 (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // 计算投影和视图转换
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // 绘制三角形
        mTriangle = Triangle()
        mTriangle.draw(mMVPMatrix)
    }

    /**
     * 创建顶点着色器
     * @Parameter [type]顶点着色器类型（GLES31.GL_VERTEX_SHADER）或片段着色器类型（GLES31.GL_FRAGMENT_SHADER）
     */
    companion object {
        fun loadShader(type: Int, shaderCode: String): Int {
            // 创建一个着色器对象
            val shader = GLES31.glCreateShader(type)

            // 将源代码添加到着色器并进行编译
            GLES31.glShaderSource(shader, shaderCode)
            GLES31.glCompileShader(shader)

            return shader
        }

    }

}