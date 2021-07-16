package com.naimyag.androiddevkit.utils

import android.content.Context
import androidx.annotation.Keep

@Keep
interface IContext {
    fun getmContext(): Context?
}