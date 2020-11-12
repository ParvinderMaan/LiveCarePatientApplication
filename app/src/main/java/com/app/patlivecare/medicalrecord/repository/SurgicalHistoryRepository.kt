package com.app.patlivecare.medicalrecord.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryInfo
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryResponse
import com.app.patlivecare.medicalrecord.model.SurgicalHistoryInfo
import com.app.patlivecare.medicalrecord.model.SurgicalHistoryResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SurgicalHistoryRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun addSurgicalHistory(headers: HashMap<String, String>, inn: SurgicalHistoryInfo): Flow<GeneralResponse> {
        return flow {
            val result = webService.addSurgicalHistory(headers, inn)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun deleteSurgicalHistory(headers: HashMap<String, String>, id: Int): Flow<GeneralResponse> {
        return flow {
            val result = webService.deleteSurgicalHistory(headers,id)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun fetchSurgicalHistory(headers: HashMap<String, String>): Flow<SurgicalHistoryResponse> {
        return flow {
            val result = webService.fetchSurgicalHistory(headers)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}