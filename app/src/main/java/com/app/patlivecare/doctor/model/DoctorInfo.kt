package com.app.patlivecare.doctor.model

import android.os.Parcelable
import com.app.patlivecare.appointment.model.TimeSlotInfo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DoctorInfo(
    @SerializedName("doctorSpecialities") var doctorSpecialities: List<SpecialityInfo> = emptyList(),
    @SerializedName("id") var id: String="",
    @SerializedName("name") var name: String="",
    @SerializedName("profilePic") var profilePic: String?=null,
    @SerializedName("rating") var rating: Int=0,
    @SerializedName("appointmentFees") var appointmentFees: Int=0,
    @Transient var date: String?=null,
    @Transient var timeSlotInfo : TimeSlotInfo?=null):Parcelable
