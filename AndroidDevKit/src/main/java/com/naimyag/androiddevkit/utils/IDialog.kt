package com.naimyag.androiddevkit.utils

import android.app.ProgressDialog
import android.content.Context
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog

@Keep
interface IDialog : IContext {

    fun infiniteLoadingCircle(
        message: String,
        cancelable: Boolean = false,
        percentage: Boolean = false,
        context: Context? = null
    ): ProgressDialog? {
        (getmContext() ?: context)?.let { ctx ->
            val p = ProgressDialog(ctx)
            p.setCancelable(cancelable)
            if (percentage) {
                p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                p.max = 100
            }
            p.setMessage(message)

            return p
        } ?: kotlin.run {
            return null
        }
    }

    fun hideLoadingCircle(p: ProgressDialog?) {
        try {
            p?.dismiss()
        } catch (e: Exception) {
        }
    }

    fun showDialog(
        title: String,
        message: String?,
        cancelable: Boolean = true,
        positiveButtonText: String? = null,
        context: Context? = null,
        callBack: ((success: Boolean) -> Unit)? = null
    ): AlertDialog? {
        (getmContext() ?: context)?.let { ctx ->
            if (message == null) {
                callBack?.let { it(false) }
                return null
            }

            var mCallback: ((Boolean) -> Unit)? = callBack
            val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(ctx)

            alertBuilder.setCancelable(cancelable)
            if (cancelable) {
                alertBuilder.setNegativeButton(android.R.string.cancel) { _, _ ->
                    mCallback?.let { it(false) }
                    mCallback = null
                }
            }

            alertBuilder.setTitle(title)
            alertBuilder.setMessage(message)

            var posText = positiveButtonText
            if (posText == null)
                posText = ctx.getString(android.R.string.ok)

            alertBuilder.setPositiveButton(posText) { _, _ ->
                mCallback?.let { it(true) }
                mCallback = null
            }

            alertBuilder.setOnCancelListener() {
                mCallback?.let { it(false) }
                mCallback = null
            }
            val alert: AlertDialog = alertBuilder.create()
            alert.show()

            return alert
        } ?: kotlin.run {
            return null
        }
    }

    fun hideDialog(dialog: AlertDialog?) {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
        }
    }


}