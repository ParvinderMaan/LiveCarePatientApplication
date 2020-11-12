package com.app.patlivecare.medicalrecord.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.medicalrecord.model.FamilyHistoryInfo
import com.app.patlivecare.medicalrecord.model.FamilyHistoryResponse
import com.app.patlivecare.medicalrecord.model.SurgicalHistoryInfo
import com.app.patlivecare.medicalrecord.model.SurgicalHistoryResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FamilyHistoryRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun addFamilyHistory(headers: HashMap<String, String>, inn: FamilyHistoryInfo): Flow<GeneralResponse> {
        return flow {
            val result = webService.addFamilyHistory(headers, inn)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun deleteFamilyHistory(headers: HashMap<String, String>, id: Int): Flow<GeneralResponse> {
        return flow {
            val result = webService.deleteFamilyHistory(headers,id)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun fetchFamilyHistory(headers: HashMap<String, String>): Flow<FamilyHistoryResponse> {
        return flow {
            val result = webService.fetchFamilyHistory(headers)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}