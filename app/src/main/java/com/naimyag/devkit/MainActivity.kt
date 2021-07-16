package com.naimyag.devkit

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.naimyag.androiddevkit.api.ApiCall
import com.naimyag.androiddevkit.utils.ToastType
import com.naimyag.androiddevkit.views.BaseActivity
import com.naimyag.devkit.model.Besin
import java.lang.Thread.sleep

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val p = infiniteLoadingCircle("Please wait...")
        p?.show()
        ApiCall.getJArray("https://raw.githubusercontent.com/atilsamancioglu/BTK20-JSONVeriSeti/master/besinler.json") { resp ->
            Thread {
                sleep(3000)
                runOnUiThread {
                    hideLoadingCircle(p)
                }
                val besinList = ApiCall.parseResponse(resp, Array<Besin>::class.java) as Array<Besin>
                besinList.forEach { printLog("besin: $it") }
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