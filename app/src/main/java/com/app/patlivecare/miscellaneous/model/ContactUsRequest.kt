package com.app.patlivecare.miscellaneous.model

data class ContactUsRequest(
    var email: String="",
    var message: String="",
    var mobileNo: String="",
    var name: String=""
)