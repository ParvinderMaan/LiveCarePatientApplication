package com.app.patlivecare.password.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.network.WebService
import com.app.patlivecare.password.model.SendOtpResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class VerificationRepository(val webService: WebService)  {

    @ExperimentalCoroutinesApi
    suspend fun attemptSendOtpAtEmail(jsonObject:JsonObject): Flow<SendOtpResponse> {
        return flow{
            val result = webService.attemptSendOtpAtEmail(jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun attemptVerifyEmail(
        headers: HashMap<String, String>,
        jsonObject: JsonObject
    ): Flow<GeneralResponse> {
        return flow{
            val result = webService.attemptVerifyEmail(headers,jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


    @ExperimentalCoroutinesApi
    suspend fun attemptSendOtpAtPhone(headers: Map<String, String>,
                                      jsonObject:JsonObject): Flow<GeneralResponse> {
        return flow{
            val result = webService.attemptSendOtpAtPhone(headers,jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun attemptVerifyPhone(headers: HashMap<String, String>, jsonObject: JsonObject): Flow<GeneralResponse> {
        return flow{
            val result = webService.attemptVerifyPhone(headers,jsonObject)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

}