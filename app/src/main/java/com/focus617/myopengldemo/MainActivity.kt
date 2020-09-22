package com.focus617.myopengldemo

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    // 实例化一个GLSurfaceView
    private lateinit var mGLSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView = XGLSurfaceView(this)
        setContentView(mGLSurfaceView)
    }
}