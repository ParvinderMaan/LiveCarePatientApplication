package com.app.patlivecare.signup.repository

import com.app.patlivecare.network.WebService
import com.app.patlivecare.signup.model.SignUpRequest
import com.app.patlivecare.signup.model.SignUpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class SignUpRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun attemptSignUp(signUpRequest: SignUpRequest): Flow<SignUpResponse> {
      //  return webService.attemptSignUp(signUpRequest)
        return flow{
            val result = webService.attemptSignUp(signUpRequest)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


}