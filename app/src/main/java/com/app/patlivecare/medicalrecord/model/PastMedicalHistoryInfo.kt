package com.app.patlivecare.medicalrecord.model

data class PastMedicalHistoryInfo(
    var id:Int=0,
    var date: String="",
    var description: String="",
    var doctorName: String="",
    var treatmentName: String=""
){
    override fun toString(): String {
        return "PastMedicalHistoryInfo(id=$id, date='$date', description='$description', doctorName='$doctorName', treatmentName='$treatmentName')"
    }
}