package com.app.patlivecare.medicalrecord.model

data class AttachmentHistoryInfo(
    var date: String,
    var description: String,
    var id: Int,
    var medicalDocumentPath: String,
    var reportName: String,
    var userId: Any
)