package com.app.patlivecare.rating.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.network.WebService
import com.app.patlivecare.rating.model.DoctorRatingResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DoctorRatingRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun fetchDoctorAppointmentRating(token: Map<String, String>,appointmentId:String): Flow<DoctorRatingResponse> {
        return flow {
            val result = webService.fetchDoctorAppointmentRating(token,appointmentId)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    suspend fun addDoctorRatingAndReview(token: Map<String, String>,inn: JsonObject): Flow<GeneralResponse> {
        return flow {
            val result = webService.addDoctorRatingAndReview(token,inn)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

}