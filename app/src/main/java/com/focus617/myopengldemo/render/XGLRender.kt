package com.focus617.myopengldemo.render

import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class XGLRender : GLSurfaceView.Renderer {

    private lateinit var mTriangle: Triangle

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // 设置渲染的位置和大小
        GLES30.glViewport(0, 0, width, height);
    }

    override fun onDrawFrame(unused: GL10) {
        // 重绘背景颜色
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle = Triangle()
        mTriangle.draw()
    }

    /**
     * 创建顶点着色器
     * @Parameter [type]顶点着色器类型（GLES30.GL_VERTEX_SHADER）或片段着色器类型（GLES30.GL_FRAGMENT_SHADER）
     */
    companion object {
        fun loadShader(type: Int, shaderCode: String): Int {
            // 创建一个着色器对象
            val shader = GLES30.glCreateShader(type)

            // 将源代码添加到着色器并进行编译
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

            return shader
        }

    }

}