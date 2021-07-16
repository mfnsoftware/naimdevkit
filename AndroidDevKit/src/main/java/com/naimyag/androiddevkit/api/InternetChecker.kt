package com.naimyag.androiddevkit.api

import androidx.annotation.Keep
import com.naimyag.androiddevkit.utils.ILog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.InetSocketAddress
import java.net.Socket

@Keep
object InternetChecker : ILog {

    fun isConnected(responseListener: (Boolean) -> Unit) {

        Observable.fromCallable {
            try {
                val timeOut = 1500
                val socket = Socket()
                val socketAddr = InetSocketAddress("www.google.com", 80)
                socket.connect(socketAddr, timeOut)
                socket.close()
                true
            } catch (e: Exception) {
                printEx("isConnected() ex:", e)
                false
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                responseListener(it)
            }
    }

    override fun getmSimpleName(): String {
        return javaClass.simpleName
    }

}