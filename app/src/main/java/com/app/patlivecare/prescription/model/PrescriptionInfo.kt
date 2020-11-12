package com.app.patlivecare.prescription.model

data class PrescriptionInfo(
    var id: Int,
    var title: String,
    var desc: String,
    var appointmentId: Int
)