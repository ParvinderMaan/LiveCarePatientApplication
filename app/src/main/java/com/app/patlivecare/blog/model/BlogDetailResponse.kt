package com.app.patlivecare.blog.model

import com.google.gson.annotations.SerializedName

data class BlogDetailResponse(
    @SerializedName("code")  var code: Int,
    @SerializedName("data")  var blogInfo: BlogInfo,
    @SerializedName("message")  var message: String,
    @SerializedName("status")  var status: Boolean
)