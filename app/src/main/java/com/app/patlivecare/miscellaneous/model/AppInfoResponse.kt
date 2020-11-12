package com.app.patlivecare.miscellaneous.model

data class AppInfoResponse(
    var code: Int,
    var data: Data,
    var message: String,
    var status: Boolean
){
    data class Data(
        var aboutUs: String,
        var privacyPolicy: String,
        var termsConditions: String
    )
}