package com.app.patlivecare.medicalrecord.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryInfo
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PastMedicalHistoryRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun addPastMedicalHistory(headers: HashMap<String, String>, inn: PastMedicalHistoryInfo): Flow<GeneralResponse> {
        return flow {
            val result = webService.addPastMedicalHistory(headers, inn)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun deletePastMedicalHistory(headers: HashMap<String, String>, id: Int): Flow<GeneralResponse> {
        return flow {
            val result = webService.deletePastMedicalHistory(headers,id)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun fetchPastMedicalHistory(headers: HashMap<String, String>): Flow<PastMedicalHistoryResponse> {
        return flow {
            val result = webService.fetchPastMedicalHistory(headers)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}