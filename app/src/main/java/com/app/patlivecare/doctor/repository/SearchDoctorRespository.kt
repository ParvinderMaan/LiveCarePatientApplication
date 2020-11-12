package com.app.patlivecare.doctor.repository

import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.doctor.model.DoctorResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SearchDoctorRespository(val webService: WebService)  {

    @ExperimentalCoroutinesApi
    suspend fun fetchSearchDoctor(headers: Map<String, String>,queryText:String): Flow<DoctorResponse> {
        return flow {
            val result = webService.fetchSearchDoctor(headers,1,searchQuery = queryText,specialityId="",pageSize = 20)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}