package com.app.patlivecare.password.model

data class ForgotPasswordResponse(
    var code: Int,
    var data: Data,
    var message: String,
    var status: Boolean
) {
    data class Data(
        var otpCode: Int
    )
}