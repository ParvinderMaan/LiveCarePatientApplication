package com.app.patlivecare.password.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.network.WebService
import com.app.patlivecare.password.model.ForgotPasswordResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ForgotPasswordRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun attemptForgotPassword(jsonObject: JsonObject): Flow<ForgotPasswordResponse> {
        return flow {
            val result = webService.attemptForgotPassword(jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    @ExperimentalCoroutinesApi
    suspend fun attemptResetPassword(jsonObject: JsonObject): Flow<GeneralResponse> {
        return flow {
            val result = webService.attemptResetPassword(jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}