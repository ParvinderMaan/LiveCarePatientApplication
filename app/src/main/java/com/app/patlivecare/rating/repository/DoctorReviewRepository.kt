package com.app.patlivecare.rating.repository

import com.app.patlivecare.rating.model.DoctorReviewResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DoctorReviewRepository (val webService: WebService) {
    @ExperimentalCoroutinesApi
    suspend fun fetchDoctorReview(token: Map<String, String>,doctorId:String): Flow<DoctorReviewResponse> {
        return flow {
            val result = webService.fetchDoctorReview(token,doctorId)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}