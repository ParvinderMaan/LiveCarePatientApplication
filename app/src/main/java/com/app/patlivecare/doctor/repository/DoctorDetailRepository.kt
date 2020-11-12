package com.app.patlivecare.doctor.repository

import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DoctorDetailRepository(val webService: WebService) {

        @ExperimentalCoroutinesApi
        suspend fun fetchDoctorDetails(token: Map<String, String>,doctorId:String): Flow<DoctorDetailResponse> {
            return flow {
                val result = webService.fetchDoctorDetails(token,doctorId)
                emit(result)
            }.flowOn(Dispatchers.IO)
        }




}