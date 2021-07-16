package com.naimyag.androiddevkit.utils

import android.util.Log
import androidx.annotation.Keep

@Keep
interface ILog : ITAG {
    private fun TAG(): String {
        return "NDT_${getmSimpleName()}"
    }

    fun printLog(msg: String) {
        Log.i(TAG(), msg)
    }

    fun printEx(msg: String, e: Exception? = null) {
        try {
            e?.let { ex ->
                ex.message?.let {
                    Log.e(TAG(), "$msg $it")
                }
                ex.printStackTrace()
            } ?: kotlin.run {
                Log.e(TAG(), msg)
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
        }
    }

}