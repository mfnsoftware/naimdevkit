package com.naimyag.devkit

import android.content.Context
import android.os.Bundle
import com.naimyag.androiddevkit.api.ApiCall
import com.naimyag.androiddevkit.api.Result
import com.naimyag.androiddevkit.views.BaseActivity
import com.naimyag.devkit.model.Besin
import java.lang.Thread.sleep

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val p = infiniteLoadingCircle("Please wait...")
        p?.show()
        ApiCall.getJArray("https://raw.githubusercontent.com/atilsamancioglu/BTK20-JSONVeriSeti/master/besinler1.json") { resp ->
            Thread {
                sleep(3000)
                runOnUiThread {
                    hideLoadingCircle(p)
                }

                when (resp) {
                    is Result.Success -> {
                        val besinList = ApiCall.parseResponse(
                            resp.data,
                            Array<Besin>::class.java
                        ) as Array<Besin>
                        besinList.forEach { printLog("besin: $it") }
                    }
                    is Result.Error -> {
                        when (resp.exception.response()!!.code()){
                            401 -> {
                                printEx("onCreate() getBesinler 401 ex:", resp.exception)
                                //for example
                                printEx("onCreate() getBesinler session closed by server, user logout!")
                            }
                            402 -> {printEx("onCreate() getBesinler 402 ex:", resp.exception)}
                            403 -> {printEx("onCreate() getBesinler 403 ex:", resp.exception)}
                            404 -> {printEx("onCreate() getBesinler 404 ex:", resp.exception)}
                            else -> {
                                printEx("onCreate() getBesinler ex:", resp.exception)
                            }
                        }

                    }
                }

            }.start()

        }

    }

    override fun getmContext(): Context {
        return this
    }

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

}