package com.app.patlivecare.medicalrecord.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.medicalrecord.model.AllergyHistoryInfo
import com.app.patlivecare.medicalrecord.model.AllergyHistoryResponse
import com.app.patlivecare.medicalrecord.model.FamilyHistoryInfo
import com.app.patlivecare.medicalrecord.model.FamilyHistoryResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AllergyHistoryRepository (val webService: WebService) {
    @ExperimentalCoroutinesApi
    suspend fun addAllergyHistory(headers: HashMap<String, String>, inn: AllergyHistoryInfo): Flow<GeneralResponse> {
        return flow {
            val result = webService.addAllergyHistory(headers, inn)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun deleteAllergyHistory(headers: HashMap<String, String>, id: Int): Flow<GeneralResponse> {
        return flow {
            val result = webService.deleteAllergyHistory(headers,id)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun fetchAllergyHistory(headers: HashMap<String, String>): Flow<AllergyHistoryResponse> {
        return flow {
            val result = webService.fetchAllergyHistory(headers)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}