package com.app.patlivecare.profile.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class StateInfoResponse(
    @SerializedName("code") var code: Int,
    @SerializedName("data") var data: Data,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean
){
    data class Data(@SerializedName("list") var listOfStates:List<State>)

    @Parcelize
    data class State(@SerializedName("id") var id: Int,
                       @SerializedName("name") var name: String): Parcelable
}