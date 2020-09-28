package com.focus617.myopengldemo

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var mGLSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建一个GLSurfaceView实例,并将其设置为此Activity的ContentView。
        mGLSurfaceView = XGLSurfaceView(this)
        setContentView(mGLSurfaceView)
    }

    override fun onResume() {
        super.onResume()
        // 恢复渲染线程，如果有必要的话重新创建OpenGL上下文，它和onPause对应
        mGLSurfaceView.onResume()
    }


    override fun onPause() {
        super.onPause()
        // 暂停渲染线程
        mGLSurfaceView.onPause()
    }
}