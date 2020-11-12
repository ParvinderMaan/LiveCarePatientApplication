package com.app.patlivecare.medicalrecord.model

import com.google.gson.annotations.SerializedName

data class AttachmentHistoryResponse(
    @SerializedName("code")var code: Int,
    @SerializedName("data") var data: MutableList<AttachmentHistoryInfo>,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean){

}