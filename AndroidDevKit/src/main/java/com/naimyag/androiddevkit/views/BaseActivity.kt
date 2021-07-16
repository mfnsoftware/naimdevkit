package com.naimyag.androiddevkit.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naimyag.androiddevkit.utils.IDialog
import com.naimyag.androiddevkit.utils.ILog
import com.naimyag.androiddevkit.utils.IToast

abstract class BaseActivity : AppCompatActivity(), IToast, IDialog, ILog, IContentView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printLog("->onCreate()")
        setContentView(getContentView())
    }

    override fun onResume() {
        super.onResume()
        printLog("->onResume()")
    }

    override fun getmSimpleName(): String? {
        return getmContext()?.javaClass?.simpleName
    }

}