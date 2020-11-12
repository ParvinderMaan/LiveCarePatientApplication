package com.app.patlivecare.medicalrecord.model

data class FamilyHistoryInfo(
    var id:Int=0,
    var age: Int=0,
    var description: String="",
    var diseaseName: String="",
    var memberName: String="",
    var relation: String=""
)