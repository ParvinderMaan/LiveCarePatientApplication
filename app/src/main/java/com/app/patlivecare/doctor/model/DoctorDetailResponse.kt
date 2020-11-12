package com.app.patlivecare.doctor.model

data class DoctorDetailResponse(
    var code: Int,
    var data: Data,
    var message: String,
    var status: Boolean
){
    data class Data(
        var about: String,
        var appointmentFees: Int,
        var doctorEducation: List<Education>,
        var doctorLangauges: List<Language>,
        var doctorSpecialities: List<SpecialityInfo>,
        var name: String,
        var profilePic: String,
        var rating: Int,
        var reviewsCount: Int
    )

    data class Language( var id: Int, var name: String)

    data class Education( var id: Int, var instituteName: String, var degreeName: String)
}