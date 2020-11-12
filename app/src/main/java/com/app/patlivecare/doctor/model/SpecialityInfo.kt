package com.app.patlivecare.doctor.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpecialityInfo(
    @SerializedName("id") var id:Int,
    @SerializedName("name") var name:String,
    @SerializedName("imagePath") var imgUrl:String?,
    @SerializedName("isActive") var isActive:Boolean):Parcelable
