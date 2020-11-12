package com.app.patlivecare.consult.model

import android.os.Parcelable
import com.app.patlivecare.doctor.model.SpecialityInfo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppointmentInfo(
    @SerializedName("appointmentId") var appointmentId: String,
    @SerializedName("appointmentStatus") var appointmentStatus: Int,
    @SerializedName("date") var date: String,
    @SerializedName("doctorId") var doctorId: String,
    @SerializedName("doctorSpecialities") var doctorSpecialities: List<SpecialityInfo>,
    @SerializedName("dr_FirstName") var firstName: String,
    @SerializedName("dr_LastName") var lastName: String,
    @SerializedName("dr_ProfilePic") var profilePic: String?,
    @SerializedName("patientId") var patientId: String,
    @SerializedName("paymentStatus") var paymentStatus: Int,
    @SerializedName("slotFrom") var slotFrom: String,
    @SerializedName("slotTo") var slotTo: String,
    @SerializedName("timeSlotId") var timeSlotId: Int,
    @Transient var remainingTime: Long=-1

):Parcelable