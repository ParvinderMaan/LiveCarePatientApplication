package com.app.patlivecare.blog.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlogInfo(
    @SerializedName("blogImagePath") var imgUrl: String,
    @SerializedName("createdDate") var createdDate: String,
    @SerializedName("description") var description: String,
    @SerializedName("doctorName") var doctorName: String,
    @SerializedName("doctorImagePath") var doctorProfilePic: String,
    @SerializedName("id") var id: String,
    @SerializedName("title") var title: String,
    @SerializedName("userId") var userId: String
):Parcelable