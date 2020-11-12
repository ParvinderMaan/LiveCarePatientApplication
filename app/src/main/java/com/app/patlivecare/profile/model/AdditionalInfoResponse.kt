package com.app.patlivecare.profile.model

import com.google.gson.annotations.SerializedName

data class AdditionalInfoResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: AdditionalProfileInfo,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Boolean
)