package com.naimyag.androiddevkit.api

interface IHeader {
    fun getHeaders(): HashMap<String, String>?
    fun setHeaders(headersMap: HashMap<String, String>?)
}