package com.app.patlivecare.medicalrecord.model

data class SurgicalHistoryInfo(
    var id:Int=0,
    var date: String="",
    var description: String="",
    var doctorName: String="",
    var treatmentName: String=""
)