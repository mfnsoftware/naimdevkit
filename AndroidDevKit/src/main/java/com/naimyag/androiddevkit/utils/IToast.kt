package com.naimyag.androiddevkit.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Keep
import com.github.andreilisun.swipedismissdialog.SwipeDismissDialog
import com.naimyag.androiddevkit.R

@Keep
interface IToast : IContext {

    fun showToast(
        detailTxt: String?,
        type: ToastType,
        headerTxt: String? = null,
        context: Context? = null
    ): SwipeDismissDialog? {
        if (detailTxt == null)
            return null

        try {
            (getmContext() ?: context)?.let { ctx ->
                val dialog: View = (ctx.applicationContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                    .inflate(R.layout.dialog_success_booking, null)
                val ll: LinearLayout = dialog.findViewById(R.id.ll_dialog)
                val img: ImageView = ll.findViewById(R.id.imgView)
                val header: TextView = ll.findViewById(R.id.header)
                val detail: TextView = ll.findViewById(R.id.detail)
                when (type) {
                    ToastType.info -> img.setImageResource(R.drawable.toast_info_v)
                    ToastType.alert -> img.setImageResource(R.drawable.toast_alert_v)
                    ToastType.warning -> img.setImageResource(R.drawable.toast_warning_v)
                    ToastType.success -> img.setImageResource(R.drawable.toast_success_v)
                }

                headerTxt?.let {
                    header.text = it
                } ?: kotlin.run {
                    header.visibility = View.GONE
                }

                detail.text = detailTxt

                val toast = SwipeDismissDialog.Builder(ctx).setView(dialog).build()
                toast.show()
                return toast
            } ?: kotlin.run {
                return null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun hideToast(dialog: SwipeDismissDialog?) {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
        }
    }
}