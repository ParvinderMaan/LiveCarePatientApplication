package com.app.patlivecare.appointment.repository

import com.app.patlivecare.appointment.model.DoctorScheduleResponse
import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.home.model.MergeResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip

class AppointmentBookingRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun fetchDoctorSchedule(token: Map<String, String>,doctorId:String,dateOfMonth:String): Flow<DoctorScheduleResponse> {
        return flow {
            emit(webService.fetchDoctorSchedule(token, doctorId, dateOfMonth))
        }.flowOn(Dispatchers.IO)
    }
}