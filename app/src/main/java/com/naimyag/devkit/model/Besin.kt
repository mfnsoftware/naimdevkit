package com.naimyag.devkit.model

import com.google.gson.annotations.SerializedName

data class Besin(
    @SerializedName("isim")
    val besinIsim: String?,
    @SerializedName("kalori")
    val besinKalori: String?,
    @SerializedName("karbonhidrat")
    val besinKarbonhidrat: String?,
    @SerializedName("protein")
    val besinProtein: String?,
    @SerializedName("yag")
    val besinYag: String?,
    @SerializedName("gorsel")
    val besinGorsel: String?
) {
    override fun toString(): String {
        return "isim: $besinIsim, kalori: $besinKalori"
    }
}
