package com.focus617.myopengldemo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.focus617.myopengldemo.render.AirHockeyRendererEs3
import com.focus617.myopengldemo.render.AirHockeyRendererEs3.Companion.Shape

/**
 * Description:
 * 暂时可以直接使用 GLSurfaceView，但后面用到捕获触摸事件来进行交互时候就需要扩展这个类了。
 */
class XGLSurfaceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private var mRenderer: AirHockeyRendererEs3? = null
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
        mRenderer = renderer as AirHockeyRendererEs3

        // 仅在绘图数据发生更改时才渲染视图: 在该模式下当渲染内容变化时不会主动刷新效果，需要手动调用requestRender()
        //renderMode = RENDERMODE_WHEN_DIRTY
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun setupShape(shape: Shape) {
        mRenderer?.setupShape(shape)
    }

    /*
    * 通过触摸事件获取要求视图矩阵旋转的角度
    */
//    private val TOUCH_SCALE_FACTOR = 180.0f / 720
//
//    // 记录上个事件时的坐标
//    private var mPreviousX = 0f
//    private var mPreviousY = 0f
//
//    override fun onTouchEvent(e: MotionEvent): Boolean {
//
//        // MotionEvent报告触摸屏和其他输入控件的输入详细信息。
//        // 在这种情况下，这里只对触摸位置发生变化的事件感兴趣。
//        val x = e.x
//        val y = e.y
//        when (e.action) {
//            MotionEvent.ACTION_MOVE -> {
//                var dx = x - mPreviousX
//                var dy = y - mPreviousY
//
//                // reverse direction of rotation above the mid-line
//                if (y > height / 2) {
//                    dx *= -1
//                }
//
//                // reverse direction of rotation to left of the mid-line
//                if (x < width / 2) {
//                    dy *= -1
//                }
//                mRenderer?.getAngle()?.minus((dx + dy) * TOUCH_SCALE_FACTOR)?.let {
//                    mRenderer?.setAngle(it)
//                }
//                requestRender()
//            }
//        }
//        mPreviousX = x
//        mPreviousY = y
//        return true
//    }


}