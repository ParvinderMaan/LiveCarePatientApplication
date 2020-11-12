package com.app.patlivecare.signup.model

data class SignUpResponse(
    val code: Int,
    val data: Data,
    val message: String,
    val status: Boolean
){
    data class Data(
        val accessToken: String,
        val city: String,
        val country: String,
        val currentAddress: String,
        val dateOfBirth: String,
        val dialCode: String,
        val email: String,
        val firstName: String,
        val gender: Int,
        val isEmailVerified: Boolean,
        val isPhoneNoVerified: Boolean,
        val emailNotificationStatus:Boolean,
        val smsNotificationStatus:Boolean,
        val lastName: String,
        val phoneNumber: String,
        val profilePic: String,
        val state: String,
        val isSocialUser:Boolean,
        val percentageProfileComplete: Int
    )
}