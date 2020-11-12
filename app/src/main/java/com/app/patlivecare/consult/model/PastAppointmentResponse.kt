package com.app.patlivecare.consult.model


data class PastAppointmentResponse (
     var code: Int,
     var data: Data,
     var message: String,
     var status: Boolean){
    data class Data(
        var currentPage: Int,
        var dataList: List<AppointmentInfo>,
        var nextPage: String,
        var pageSize: Int,
        var previousPage: String,
        var searchQuery: String,
        var totalCount: Int,
        var totalPages: Int
    )
}