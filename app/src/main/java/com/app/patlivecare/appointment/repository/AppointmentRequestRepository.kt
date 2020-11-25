package com.app.patlivecare.appointment.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AppointmentRequestRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun attemptAppointmentRequest(
        token: Map<String, String>,
        inn: JsonObject
    ): Flow<GeneralResponse> {
        return flow {
            emit(webService.attemptAppointmentRequest(token, inn))
        }
            .flowOn(Dispatchers.IO)
    }
}