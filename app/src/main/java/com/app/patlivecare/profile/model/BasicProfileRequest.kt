package com.app.patlivecare.profile.model

data class BasicProfileRequest(
    val city: String="",
    val countryId: Int=0,
    val currentAddress: String="",
    val dateOfBirth: String="",
    val dialCode: String="",
    val firstName: String="",
    val genderId: Int=1,
    val lastName: String="",
    val phoneNumber: String="",
    val stateId: Int=0,
    val state: String="", // not used in request
    val country: String="" // not used in request
)