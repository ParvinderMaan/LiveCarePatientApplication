package com.app.patlivecare.miscellaneous.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.miscellaneous.model.ContactUsRequest
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ContactUsRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun attemptContactUs(headers: Map<String, String>, inn: ContactUsRequest): Flow<GeneralResponse> {
        return flow {
            val result = webService.attemptContactUs(headers, inn)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}