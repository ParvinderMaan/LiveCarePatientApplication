package com.app.patlivecare.home.model

data class PatientInfoResponse(
    var code: Int,
    var data: Data,
    var message: String,
    var status: Boolean
){
    data class Data(
        var firstName: String,
        var lastName: String,
        var percentageProfileComplete: Int)
}