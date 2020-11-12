package com.app.patlivecare.consult.model

import com.google.gson.annotations.SerializedName

data class UpcomingAppointmentResponse(
    @SerializedName("code") var code: Int,
    @SerializedName("data") var data: List<AppointmentInfo>,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean
)