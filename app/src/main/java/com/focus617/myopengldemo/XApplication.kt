package com.focus617.myopengldemo

import android.app.Application
import timber.log.Timber

class XApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())		// Java 需要 new Timber.DebugTree()
    }
}