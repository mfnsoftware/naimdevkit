package com.naimyag.androiddevkit.api

import androidx.annotation.Keep

@Keep
interface DownloadCallbacks {
    fun onNext(t: String?)
    fun onError(t: Throwable?)
    fun onComplete()
}