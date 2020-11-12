package com.app.patlivecare.profile.repository

import com.app.patlivecare.network.WebService
import com.app.patlivecare.signin.model.SignInRequest
import com.app.patlivecare.signup.model.SignUpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProfileAdditionalRepository (val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun attemptSignIn(headers:Map<String, String>,signInRequest: SignInRequest): Flow<SignUpResponse> {
        //  return webService.attemptSignUp(signUpRequest)
        return flow{
            val result = webService.attemptSignIn(headers,signInRequest)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


}