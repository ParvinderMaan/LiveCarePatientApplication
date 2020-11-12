package com.app.patlivecare

data class GeneralResponse(
    val code: Int,
    val data: Data,
    val message: String,
    val status: Boolean
){
    class Data()
}