package com.naimyag.androiddevkit.base

import android.app.Application
import com.naimyag.androiddevkit.prefs.UpCatcher
import com.naimyag.androiddevkit.utils.IContext
import com.naimyag.androiddevkit.utils.ILog

abstract class BaseApplication : Application(), IContext, ILog {

    companion object {
        lateinit var upCatcher: UpCatcher
    }

    override fun onCreate() {
        super.onCreate()
        printLog("->onCreate()")
        upCatcher = UpCatcher((getmContext() ?: this).applicationContext, "APP_PREF")

    }

}