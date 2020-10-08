package com.focus617.myopengldemo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focus617.myopengldemo.render.AirHockeyRendererEs3
import com.focus617.myopengldemo.render.ParticlesRenderer
import com.focus617.myopengldemo.util.TextureHelper.FilterMode

class MainActivity : AppCompatActivity() {

    /**
     * Hold a reference to our GLSurfaceViewr
     */
    private lateinit var mGLSurfaceView: XGLSurfaceView

    private var hasSetRenderer: Boolean = false
    private lateinit var particlesRenderer: ParticlesRenderer
    private lateinit var airHockeyRenderer: AirHockeyRendererEs3

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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // 创建一个GLSurfaceView实例,并将其设置为此Activity的ContentView。
        mGLSurfaceView = XGLSurfaceView(this)

        if (!isES3Supported()) return

        // Request an OpenGL ES 3.0 compatible context.
        mGLSurfaceView.setEGLContextClientVersion(3)
        mGLSurfaceView.setEGLConfigChooser(MultisampleConfigChooser())

        //setAirHockeyAsRenderer()
        setParticlesAsRenderer()

        setContentView(mGLSurfaceView)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setParticlesAsRenderer() {
        // 设置渲染器（Renderer）以在GLSurfaceView上绘制
        particlesRenderer = ParticlesRenderer(this)
        mGLSurfaceView.setRenderer(particlesRenderer)
        hasSetRenderer = true

        mGLSurfaceView.setOnTouchListener(object : OnTouchListener {
            var previousX = 0f
            var previousY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        previousX = event.x
                        previousY = event.y
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.x - previousX
                        val deltaY = event.y - previousY
                        previousX = event.x
                        previousY = event.y
                        mGLSurfaceView.queueEvent {
                            particlesRenderer.handleTouchDrag(deltaX, deltaY)
                        }
                        true
                    }
                    else -> false
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setAirHockeyAsRenderer() {
        // 设置渲染器（Renderer）以在GLSurfaceView上绘制
        airHockeyRenderer = AirHockeyRendererEs3(this)
        mGLSurfaceView.setRenderer(airHockeyRenderer)
        hasSetRenderer = true

        mGLSurfaceView.setOnTouchListener(object : OnTouchListener {
            var previousX = 0f
            var previousY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                // Convert touch coordinates into normalized device
                // coordinates, keeping in mind that Android's Y
                // coordinates are inverted.
                val normalizedX = (event.x / v.width.toFloat()) * 2 - 1
                val normalizedY = -((event.y / v.height.toFloat()) * 2 - 1)

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        previousX = event.x
                        previousY = event.y
                        mGLSurfaceView.queueEvent {
                            airHockeyRenderer.handleTouchPress(normalizedX, normalizedY)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.x - previousX
                        val deltaY = event.y - previousY
                        previousX = event.x
                        previousY = event.y
                        mGLSurfaceView.queueEvent {
                            airHockeyRenderer.handleTouchDrag(
                                normalizedX,
                                normalizedY,
                                deltaX,
                                deltaY
                            )
                        }
                    }
                }
                return true
            }
        })
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

    fun onChooseFilter(filterMode: FilterMode) {
        if (!particlesRenderer.supportsAnisotropicFiltering()
            && filterMode === FilterMode.ANISOTROPIC
        ) {
            Toast.makeText(this, getString(R.string.noAnisotropicFiltering), Toast.LENGTH_LONG)
                .show()
        } else {
            mGLSurfaceView.queueEvent { particlesRenderer.handleFilterModeChange(filterMode) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        lateinit var filterMode: FilterMode

        when (item.itemId) {
            R.id.filterChoices_Nearest_neighbour -> {
                Toast.makeText(this, "Nearest neighbour", Toast.LENGTH_SHORT).show()
                filterMode = FilterMode.NEAREST_NEIGHBOUR
            }
            R.id.filterChoices_Bilinear -> {
                Toast.makeText(this, "Bilinear", Toast.LENGTH_SHORT).show()
                filterMode = FilterMode.BILINEAR
            }
            R.id.filterChoices_BilinearMipmapping -> {
                Toast.makeText(this, "Bilinear mipmapping", Toast.LENGTH_SHORT).show()
                filterMode = FilterMode.BILINEAR_WITH_MIPMAPS
            }
            R.id.filterChoices_TrilinearMipmapping -> {
                Toast.makeText(this, "Trilinear mipmapping", Toast.LENGTH_SHORT).show()
                filterMode = FilterMode.TRILINEAR
            }
            R.id.filterChoices_AnisotropicMipmapping -> {
                Toast.makeText(this, "Anisotropic mipmapping", Toast.LENGTH_SHORT).show()
                filterMode = FilterMode.ANISOTROPIC
            }
        }
        onChooseFilter(filterMode)

        return true
    }
}