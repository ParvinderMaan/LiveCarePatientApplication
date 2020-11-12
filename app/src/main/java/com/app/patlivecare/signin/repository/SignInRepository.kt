package com.app.patlivecare.signin.repository

import androidx.lifecycle.MutableLiveData
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.network.WebService
import com.app.patlivecare.signin.model.SignInRequest
import com.app.patlivecare.signin.model.SocialSignInRequest
import com.app.patlivecare.signup.model.SignUpRequest
import com.app.patlivecare.signup.model.SignUpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class SignInRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun attemptSignIn(headers:Map<String, String>,signInRequest: SignInRequest): Flow<SignUpResponse> {
      //  return webService.attemptSignUp(signUpRequest)
        return flow{
            val result = webService.attemptSignIn(headers,signInRequest)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


    @ExperimentalCoroutinesApi
    suspend fun attemptSocialSignIn(headers:Map<String, String>,signInRequest: SocialSignInRequest): Flow<SignUpResponse> {
        return flow{
            val result = webService.attemptSocialSignIn(headers,signInRequest)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }


}