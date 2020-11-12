package com.app.patlivecare.medicalrecord.model

import com.google.gson.annotations.SerializedName

data class PastMedicalHistoryResponse(
    @SerializedName("code") var code: Int,
    @SerializedName("data") var data: MutableList<PastMedicalHistoryInfo>,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean
)