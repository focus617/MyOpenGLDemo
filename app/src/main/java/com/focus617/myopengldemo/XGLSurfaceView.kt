package com.focus617.myopengldemo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.focus617.myopengldemo.render.XGLRender

/**
 * Description:
 * 暂时可以直接使用 GLSurfaceView，但后面用到捕获触摸事件来进行交互时候就需要扩展这个类了。
 */
class XGLSurfaceView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private var mRender = XGLRender()

    init {
        // 创建一个OpenGL ES 3.0 的context
        setEGLContextClientVersion(3)

        // 设置渲染器（Renderer）以在GLSurfaceView上绘制
        setRenderer(mRender)

        // 仅在绘图数据发生更改时才渲染视图
        // 在该模式下当渲染内容变化时不会主动刷新效果，需要手动调用requestRender() 才行
        renderMode = RENDERMODE_WHEN_DIRTY
    }

}