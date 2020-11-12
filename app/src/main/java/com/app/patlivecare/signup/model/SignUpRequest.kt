package com.app.patlivecare.signup.model

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("dateOfBirth") var dateOfBirth: String="",
    @SerializedName("deviceToken") var deviceToken: String="",
    @SerializedName("deviceType") var deviceType: String="",
    @SerializedName("dialCode") var dialCode: String="",
    @SerializedName("email") var email: String="",
    @SerializedName("firstName") var firstName: String="",
    @SerializedName("genderId") var genderId: Int=1,
    @SerializedName("lastName") var lastName: String="",
    @SerializedName("password") var password: String="",
    @SerializedName("phoneNumber") var phoneNumber: String=""
)
