package com.app.patlivecare.videocall.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class VideoTokenResponse(
    var code: Int,
    var data: Data,
    var message: String,
    var status: Boolean
){
    @Parcelize         // rename it to VideoCallInfo
    data class Data(
        var mediaValue: MediaValue,
        var openTokApiKey: Int
    ):Parcelable
    @Parcelize
    data class MediaValue(
        var appointmentId: String,
        var completedTime: String, //String
        var connectionId: String,
        var createdBy: String,
        var createdDate: String,
        var doctorId: String,
        var expireTime: Double,
        var id: String,
        var isCompleted: Boolean,
        var patientId: String,
        var sessionDetails: String,
        var sessionId: String,
        var sessionStatus: Int,
        var token: String
    ): Parcelable
}