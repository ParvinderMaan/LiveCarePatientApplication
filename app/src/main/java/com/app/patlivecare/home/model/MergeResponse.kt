package com.app.patlivecare.home.model

import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.model.SpecialityInfo
import com.google.gson.annotations.SerializedName

data class MergeResponse(@SerializedName("code")  var code: Int,
                         @SerializedName("message")  var message: String,
                         @SerializedName("status")  var status: Boolean,
                         @SerializedName("list") var listOfSpeciality:List<SpecialityInfo>,
                         @SerializedName("dataList") var listOfDoctor:List<DoctorInfo>)