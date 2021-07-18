package com.naimyag.devkit.base

import android.content.Context
import androidx.multidex.MultiDex
import com.naimyag.androiddevkit.base.BaseApplication
import com.naimyag.androiddevkit.prefs.UpCatcher

class MyApplication : BaseApplication() {

    companion object {
        lateinit var upCatcher: UpCatcher
    }

    override fun onCreate() {
        super.onCreate()

        upCatcher = UpCatcher(applicationContext, "APP_PREF")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)

    }

    override fun getmContext(): Context? {
        return this
    }

    override fun getmSimpleName(): String? {
        return javaClass.simpleName
    }
}