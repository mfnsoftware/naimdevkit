package com.naimyag.androiddevkit.api

import androidx.annotation.Keep

@Keep
interface UploadCallbacks {
    fun onProgressUpdate(percentage: Int)
    fun onError()
    fun onFinish(result: String?)
    fun uploadStart()
}