package com.focus617.myopengldemo

import android.opengl.GLSurfaceView.EGLConfigChooser
import timber.log.Timber
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

/**
 * Original code from:
 * https://code.google.com/p/gdc2011-android-opengl/source/browse/trunk/src/com/example/gdc11/MultisampleConfigChooser.java
 * Code license: http://www.apache.org/licenses/LICENSE-2.0  */
/**
 * This class shows how to use multisampling. To use this, call
 * myGLSurfaceView.setEGLConfigChooser(new MultisampleConfigChooser()); before
 * calling setRenderer(). Multisampling will probably slow down your app --
 * measure performance carefully and decide if the vastly improved visual
 * quality is worth the cost.
 */
class MultisampleConfigChooser : EGLConfigChooser {

    private var mValue: IntArray = IntArray(1)
    private var mUsesCoverageAa = false

    override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig {

        // Try to find a normal multisample configuration first.
        var configSpec = intArrayOf(
            EGL10.EGL_RED_SIZE, 5,
            EGL10.EGL_GREEN_SIZE, 6,
            EGL10.EGL_BLUE_SIZE, 5,
            EGL10.EGL_DEPTH_SIZE, 16,
            // Requires that setEGLContextClientVersion(2) is called on the view.
            EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
            EGL10.EGL_SAMPLE_BUFFERS, 1 /* true */,
            EGL10.EGL_SAMPLES, 2,
            EGL10.EGL_NONE
        )
        require(
            egl.eglChooseConfig(
                display,
                configSpec,
                null,
                0,
                mValue
            )
        ) { "eglChooseConfig failed" }

        var numConfigs = mValue[0]
        if (numConfigs <= 0) {
            Timber.v("%s%s%s",
                "No normal multisampling config was found. ",
                "Attempting to create a coverage multisampling configuration, ",
                "for the nVidia Tegra2. See the EGL_NV_coverage_sample documentation."
            )


            val EGL_COVERAGE_BUFFERS_NV = 0x30E0
            val EGL_COVERAGE_SAMPLES_NV = 0x30E1

            configSpec = intArrayOf(
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                EGL_COVERAGE_BUFFERS_NV, 1   /* true */,
                EGL_COVERAGE_SAMPLES_NV, 2,  // always 5 in practice on tegra 2
                EGL10.EGL_NONE
            )
            require(
                egl.eglChooseConfig(
                    display,
                    configSpec,
                    null,
                    0,
                    mValue
                )
            ) { "2nd eglChooseConfig failed" }

            numConfigs = mValue[0]
            if (numConfigs <= 0) {
                Timber.v("Could not find a multisample config. Trying without multisampling...")

                configSpec = intArrayOf(
                    EGL10.EGL_RED_SIZE, 5, EGL10.EGL_GREEN_SIZE, 6,
                    EGL10.EGL_BLUE_SIZE, 5, EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
                    EGL10.EGL_NONE
                )
                require(
                    egl.eglChooseConfig(
                        display,
                        configSpec,
                        null,
                        0,
                        mValue
                    )
                ) { "3rd eglChooseConfig failed" }
                numConfigs = mValue[0]
                require(numConfigs > 0) { "No configs match configSpec" }
            } else {
                mUsesCoverageAa = true
            }
        }

        // Get all matching configurations.
        val configs = arrayOfNulls<EGLConfig>(numConfigs)
        require(
            egl.eglChooseConfig(display, configSpec, configs, numConfigs, mValue)
        ) { "data eglChooseConfig failed" }

        // CAUTION! eglChooseConfigs returns configs with higher bit depth
        // first: Even though we asked for rgb565 configurations, rgb888
        // configurations are considered to be "better" and returned first.
        // You need to explicitly filter the data returned by eglChooseConfig!
        var index = -1
        for (i in configs.indices) {
            if (findConfigAttrib(egl, display, configs[i], EGL10.EGL_RED_SIZE, 0) == 8) {
                index = i
                break
            }
        }
        if (index == -1) {
            Timber.w("Did not find sane config, using first")
        }
        return (if (configs.isNotEmpty()) configs[index] else null)
            ?: throw IllegalArgumentException("No config chosen")
    }

    private fun findConfigAttrib(
        egl: EGL10, display: EGLDisplay,
        config: EGLConfig?, attribute: Int, defaultValue: Int
    ): Int {
        return if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
            mValue[0]
        } else defaultValue
    }

    fun usesCoverageAa(): Boolean {
        return mUsesCoverageAa
    }


}