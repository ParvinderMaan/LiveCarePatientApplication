package com.app.patlivecare.rating.model

import com.google.gson.annotations.SerializedName

data class UserReview(
    @SerializedName("date") var date: String,
    @SerializedName("name") var name: String,
    @SerializedName("patientId") var patientId: String,
    @SerializedName("profilePic") var profilePic: String,
    @SerializedName("reviews") var reviews: String
)