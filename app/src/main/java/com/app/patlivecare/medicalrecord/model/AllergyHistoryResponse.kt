package com.app.patlivecare.medicalrecord.model

import com.google.gson.annotations.SerializedName

data class AllergyHistoryResponse(
    @SerializedName("code") var code: Int,
    @SerializedName("data") var data: MutableList<AllergyHistoryInfo>,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean)