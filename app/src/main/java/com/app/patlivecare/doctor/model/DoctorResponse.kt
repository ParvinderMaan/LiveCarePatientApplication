package com.app.patlivecare.doctor.model

import com.google.gson.annotations.SerializedName

data class DoctorResponse(
    var code: Int,
    var data: Data,
    var message: String,
    var status: Boolean
){
    data class Data(
       var currentPage: Int,
        var dataList: List<DoctorInfo>,
        var nextPage: String,
       var pageSize: Int,
         var previousPage: String,
         var searchQuery: String,
        var totalCount: Int,
         var totalPages: Int
    )




}