package com.app.patlivecare.blog.model

import com.google.gson.annotations.SerializedName

data class BlogResponse(
    @SerializedName("code") var code: Int,
    @SerializedName("data") var data: Data,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Boolean){

    data class Data(
        @SerializedName("dataList") var blogList: List<BlogInfo>,
        @SerializedName("totalCount") var totalCount: Int,
        @SerializedName("pageNumber") var pageNumber: Int,
        @SerializedName("pageSize") var pageSize: Int,
        @SerializedName("currentPage") var currentPage: Int,
        @SerializedName("previousPage") var previousPage: String,
        @SerializedName("nextPage") var nextPage: String)

}

  /*
    "totalCount": 0,
    "pageSize": 10,
    "currentPage": 1,
    "totalPages": 0,
    "previousPage": "No",
    "nextPage": "No",
    "searchQuery": "no parameter passed",
    "dataList": []

   */