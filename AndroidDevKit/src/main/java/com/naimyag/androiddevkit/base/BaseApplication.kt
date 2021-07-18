package com.naimyag.androiddevkit.base

import android.app.Application
import com.naimyag.androiddevkit.utils.IContext
import com.naimyag.androiddevkit.utils.ILog

abstract class BaseApplication : Application(), IContext, ILog {

    override fun onCreate() {
        super.onCreate()
        printLog("->onCreate()")
    }
}