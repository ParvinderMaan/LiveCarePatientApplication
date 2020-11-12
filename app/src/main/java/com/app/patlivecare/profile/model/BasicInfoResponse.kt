package com.app.patlivecare.profile.model

data class BasicInfoResponse(
    val code: Int,
    val data: Data,
    val message: String,
    val status: Boolean
){
    data class Data(
        val city: String,
        val country: String,
        val currentAddress: String,
        val dateOfBirth: String,
        val dialCode: String,
        val email: String,
        val firstName: String,
        val gender: String,
        val genderId: Int,
        val countryId: Int,
        val stateId: Int,
        val isEmailVerified: Boolean,
        val isPhoneNoVerified: Boolean,
        val lastName: String,
        val phoneNumber: String,
        val profilePic: String,
        val state: String
    )
}