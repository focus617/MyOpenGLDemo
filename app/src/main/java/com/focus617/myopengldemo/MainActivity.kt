package com.focus617.myopengldemo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focus617.myopengldemo.render.AirHockeyRendererEs3

class MainActivity : AppCompatActivity() {

    /**
     * Hold a reference to our GLSurfaceViewr
     */
    private lateinit var mGLSurfaceView: XGLSurfaceView

    private var hasSetRenderer: Boolean = false

    // Check if the system supports OpenGL ES 3.0.
    private var supportsEs3 = false
    private fun isES3Supported(): Boolean {

        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo

        // Even though the latest emulator supports OpenGL ES 3.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 3.0.
        // Even though the latest emulator supports OpenGL ES 3.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 3.0.
        supportsEs3 = (configurationInfo.reqGlEsVersion >= 0x30000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"))))

        if (!supportsEs3) {
            /*
                 * This is where you could create an OpenGL ES 1.x compatible
                 * renderer if you wanted to support both ES 1 and ES 2/3. Since
                 * we're not doing anything, the app will crash if the device
                 * doesn't support OpenGL ES 3.0. If we publish on the market, we
                 * should also add the following to AndroidManifest.xml:
                 *
                 * <uses-feature android:glEsVersion="0x00030000"
                 * android:required="true" />
                 *
                 * This hides our app from those devices which don't support OpenGL
                 * ES 3.0.
                 */
            Toast.makeText(
                this, "This device does not support OpenGL ES 3.0.",
                Toast.LENGTH_LONG
            ).show()
        }
        return supportsEs3
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // 创建一个GLSurfaceView实例,并将其设置为此Activity的ContentView。
        mGLSurfaceView = XGLSurfaceView(this)

        if (!isES3Supported()) return

        // Request an OpenGL ES 3.0 compatible context.
        mGLSurfaceView.setEGLContextClientVersion(3)

        setAirHockeyAsRenderer()

        setContentView(mGLSurfaceView)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setAirHockeyAsRenderer() {
        // 设置渲染器（Renderer）以在GLSurfaceView上绘制
        //mGLSurfaceView.setRenderer(XGLRenderer(this))
        val airHockeyRenderer = AirHockeyRendererEs3(this)
        mGLSurfaceView.setRenderer(airHockeyRenderer)
        hasSetRenderer = true

        mGLSurfaceView.setOnTouchListener { v, event ->
            if (event != null) {
                // Convert touch coordinates into normalized device
                // coordinates, keeping in mind that Android's Y
                // coordinates are inverted.
                val normalizedX = (event.x / v.width.toFloat()) * 2 - 1
                val normalizedY = -((event.y / v.height.toFloat()) * 2 - 1)

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mGLSurfaceView.queueEvent {
                            airHockeyRenderer.handleTouchPress(normalizedX, normalizedY)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        mGLSurfaceView.queueEvent {
                            airHockeyRenderer.handleTouchDrag(normalizedX, normalizedY)
                        }
                    }
                }
                true
            } else {
                false
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // 恢复渲染线程，如果有必要的话重新创建OpenGL上下文，它和onPause对应
        if (hasSetRenderer) mGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // 暂停渲染线程
        if (hasSetRenderer) mGLSurfaceView.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_triangle -> {
                Toast.makeText(this, "Triangle", Toast.LENGTH_SHORT).show()
                setupRenderer(Renderer.Triangle)
                true
            }
            R.id.action_square -> {
                Toast.makeText(this, "Square", Toast.LENGTH_SHORT).show()
                setupRenderer(Renderer.Square)
                true
            }
            R.id.action_cube -> {
                Toast.makeText(this, "Cube", Toast.LENGTH_SHORT).show()
                setupRenderer(Renderer.Cube)
                true
            }
            R.id.action_air_hockey -> {
                Toast.makeText(this, "AirHockey", Toast.LENGTH_SHORT).show()
                setupRenderer(Renderer.AirHockey)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var renderer: Renderer = Renderer.AirHockey
    private fun setupRenderer(renderer: Renderer) {
        this.renderer = renderer
    }

    companion object {
        enum class Renderer {
            Unknown,
            Triangle,
            Square,
            Cube,
            AirHockey
        }
    }
}