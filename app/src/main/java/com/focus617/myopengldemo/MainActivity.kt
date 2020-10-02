package com.focus617.myopengldemo

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focus617.myopengldemo.render.AirHockeyRendererEs3
import com.focus617.myopengldemo.render.XGLRenderer.Companion.Shape

class MainActivity : AppCompatActivity() {

    /**
     * Hold a reference to our GLSurfaceViewr
     */
    private lateinit var mGLSurfaceView: XGLSurfaceView

    // Check if the system supports OpenGL ES 3.0.
    private var supportsEs3 = false
    private fun isES3Supported(){

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

        if(!supportsEs3) {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建一个GLSurfaceView实例,并将其设置为此Activity的ContentView。
        mGLSurfaceView = XGLSurfaceView(this)

        isES3Supported()
        if (supportsEs3) {
            // Request an OpenGL ES 3.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(3)

            // 设置渲染器（Renderer）以在GLSurfaceView上绘制
            //mGLSurfaceView.setRenderer(XGLRenderer(this))
            mGLSurfaceView.setRenderer(AirHockeyRendererEs3(this))
        }
        setContentView(mGLSurfaceView)
    }

    override fun onResume() {
        super.onResume()
        // 恢复渲染线程，如果有必要的话重新创建OpenGL上下文，它和onPause对应
        if (supportsEs3) mGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // 暂停渲染线程
        if (supportsEs3) mGLSurfaceView.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater;
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_triangle -> {
                Toast.makeText(this, "Triangle", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.Triangle)
                true
            }
            R.id.action_square -> {
                Toast.makeText(this, "Square", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.Square)
                true
            }
            R.id.action_cube -> {
                Toast.makeText(this, "Cube", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.Cube)
                true
            }
            R.id.action_air_hockey -> {
                Toast.makeText(this, "AirHockey", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.AirHockey)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}