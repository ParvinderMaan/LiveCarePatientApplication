package com.app.patlivecare.signin.model

data class SocialSignInRequest(
    var deviceToken: String="",
    var deviceType: String="",
    var email: String="",
    var firstName: String="",
    var lastName: String="",
    var loginType: String="",
    var socialId: String=""
)