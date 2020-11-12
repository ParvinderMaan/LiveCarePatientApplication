package com.app.patlivecare.profile.model

data class AlterProfilePicResponse (
    val data: Data,
    val message: String,
    val status: Boolean
){
    data class Data(
        val profilepicurl: String
    )
}