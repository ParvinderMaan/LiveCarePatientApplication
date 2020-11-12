package com.app.patlivecare.appointment.model

data class DoctorScheduleResponse(
    var code: Int,
    var data: List<AvailableDate>,
    var message: String,
    var status: Boolean
){
    data class AvailableDate(
        var availableDates: String,
        var fromTime: String,
        var toTime: String
    )
}
/*
{
  "status": true,
  "message": "Available dates for Patient shown successfully",
  "data": [
    "10-09-2020",
    "11-09-2020",
    "12-09-2020"
  ],
  "code": 200
}
 */