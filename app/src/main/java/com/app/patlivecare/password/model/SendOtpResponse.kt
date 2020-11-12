package com.app.patlivecare.password.model


data class SendOtpResponse (val code: Int,
                            val data: Data,
                            val message: String,
                            val status: Boolean){
    data class Data(val otpcode:Int)
}