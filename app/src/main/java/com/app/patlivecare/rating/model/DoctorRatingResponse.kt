package com.app.patlivecare.rating.model

data class DoctorRatingResponse(
    var code: Int,
    var data: Data,
    var message: String,
    var status: Boolean
){
    data class Data(
        var userRating: Int
    )
}