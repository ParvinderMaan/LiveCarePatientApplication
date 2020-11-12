package com.app.patlivecare.rating.model

import com.google.gson.annotations.SerializedName

data class DoctorReviewResponse(
    @SerializedName("code") var code: Int,
    @SerializedName("data") var data: List<UserReview>,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean
)