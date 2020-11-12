package com.app.patlivecare.videocall.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoCallInfo(
    @SerializedName("mediaValue") var mediaValue: MediaValue,
    @SerializedName("openTokApiKey") var openTokApiKey: Int):Parcelable{
    @Parcelize
    data class MediaValue(
        @SerializedName("appointmentId") var appointmentId: String,
     //   @SerializedName("completedTime") var completedTime: String,
        @SerializedName("connectionId") var connectionId: String,
        @SerializedName("createdBy") var createdBy: String,
        @SerializedName("createdDate") var createdDate: String,
        @SerializedName("doctorId") var doctorId: String,
        @SerializedName("expireTime") var expireTime: Double,
        @SerializedName("id") var id: String,
        @SerializedName("isCompleted") var isCompleted: Boolean,
        @SerializedName("patientId") var patientId: String,
     //   @SerializedName("sessionDetails")  var sessionDetails: String,
        @SerializedName("sessionId") var sessionId: String,
        @SerializedName("sessionStatus") var sessionStatus: Int,
        @SerializedName("token") var token: String
    ):Parcelable

}
