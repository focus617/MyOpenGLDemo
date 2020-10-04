package com.focus617.myopengldemo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * Description:
 * 暂时可以直接使用 GLSurfaceView，但后面用到捕获触摸事件来进行交互时候就需要扩展这个类了。
 */
class XGLSurfaceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    override fun setRenderer(renderer: Renderer?) {
        /*
      * 一个给定的Android设备可能支持多个EGLConfig渲染配置。
      * 可用的配置可能在有多少个数据通道和分配给每个数据通道的比特数上不同。
      * 默认情况下，GLSurfaceView选择的EGLConfig有RGB_888像素格式，至少有16位深度缓冲和没有模板。
      * 安装一个ConfigChooser，它将至少具有指定的depthSize和stencilSize的配置，并精确指定redSize、
      * greenSize、blueSize和alphaSize(Alpha used for plane blending)。
      * .
      */
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

        super.setRenderer(renderer)

        // 仅在绘图数据发生更改时才渲染视图: 在该模式下当渲染内容变化时不会主动刷新效果，需要手动调用requestRender()
        //renderMode = RENDERMODE_WHEN_DIRTY
        renderMode = RENDERMODE_CONTINUOUSLY
    }

}