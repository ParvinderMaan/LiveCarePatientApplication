package com.app.patlivecare.signin.model

data class SignInRequest(
    var deviceToken: String="",
    var deviceType: String="",
    var email: String="",
    var password: String=""
)