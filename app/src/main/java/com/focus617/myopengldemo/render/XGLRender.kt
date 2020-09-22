package com.focus617.myopengldemo.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class XGLRender: GLSurfaceView.Renderer {

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // 设置重绘背景框架颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // 设置渲染的位置和大小
        GLES20.glViewport(0, 0, width, height);
    }

    override fun onDrawFrame(unused: GL10) {
        // 重绘背景颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

}