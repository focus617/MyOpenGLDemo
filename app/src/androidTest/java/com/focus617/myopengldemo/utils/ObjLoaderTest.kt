package com.focus617.myopengldemo.utils

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.focus617.myopengldemo.utils.objTools.ObjLoader
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ObjLoaderTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.focus617.myopengldemo", appContext.packageName)
    }

    @Test
    fun loadFromObjFile() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        ObjLoader.load(appContext, "3dModel/statueOfPharaoh/sculpt.obj")

        assertEquals("com.focus617.myopengldemo", appContext.packageName)
    }
}