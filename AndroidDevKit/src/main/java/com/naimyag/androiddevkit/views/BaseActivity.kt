package com.naimyag.androiddevkit.views

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.naimyag.androiddevkit.utils.IDialog
import com.naimyag.androiddevkit.utils.ILog
import com.naimyag.androiddevkit.utils.IToast

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), IToast, IDialog, ILog {

    protected lateinit var binding : VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printLog("->onCreate()")
        binding = inflateLayout(layoutInflater)
        setContentView(binding.root)
    }

    abstract fun inflateLayout(layoutInflater: LayoutInflater) : VB

    override fun onResume() {
        super.onResume()
        printLog("->onResume()")
    }

    override fun getmSimpleName(): String? {
        return getmContext()?.javaClass?.simpleName
    }

}