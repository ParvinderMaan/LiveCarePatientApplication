package com.app.patlivecare.profile.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class CountryInfoResponse(
    @SerializedName("code")  var code: Int,
    @SerializedName("data") var data: Data,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean
){
    data class Data(@SerializedName("list") var listOfCountries:List<Country>)
    @Parcelize
    data class Country(@SerializedName("id") var id: Int,
                       @SerializedName("name") var name: String): Parcelable
}