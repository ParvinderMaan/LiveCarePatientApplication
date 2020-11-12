package com.app.patlivecare.appointment.model

import com.google.gson.annotations.SerializedName

data class DoctorTimeSlotResponse(
    @SerializedName("code") var code: Int,
    @SerializedName("data") var data: List<TimeSlotInfo>,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean
)
