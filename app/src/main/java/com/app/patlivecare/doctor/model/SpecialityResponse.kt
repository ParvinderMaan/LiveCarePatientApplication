package com.app.patlivecare.doctor.model

import com.google.gson.annotations.SerializedName

data class SpecialityResponse(
    @SerializedName("code")  var code: Int,
    @SerializedName("data")  var data: Data,
    @SerializedName("message")  var message: String,
    @SerializedName("status")  var status: Boolean
){
    data class Data(@SerializedName("list") var listOfSpeciality:List<SpecialityInfo>)
}