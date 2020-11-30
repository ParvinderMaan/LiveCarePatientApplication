package com.app.patlivecare.signin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.SignInType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.signin.model.SignInRequest
import com.app.patlivecare.signin.model.SocialSignInRequest
import com.app.patlivecare.signin.repository.SignInRepository
import com.app.patlivecare.signup.model.SignUpResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var loadGeneral: MutableLiveData<Boolean>
    var loadFacebook: MutableLiveData<Boolean>
    var loadGoogle: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>
    var signInRequest: SignInRequest
    var resultantSignIn: MutableLiveData<WebResponse<SignUpResponse>>
    var headerMap:Map<String, String>
    var signInRepository: SignInRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        loadGeneral = MutableLiveData()
        loadFacebook= MutableLiveData()
        loadGoogle= MutableLiveData()
        isViewEnable= MutableLiveData()
        headerMap=HashMap()
        resultantSignIn = MutableLiveData()
        signInRequest=SignInRequest()
        signInRepository= SignInRepository(webService)
    }

//    fun attemptSignIn()  {
//        isLoading.value=true
//        webService.attemptSignIn(headerMap,signInRequest)?.enqueue(object : Callback<SignUpResponse> {
//            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
//                isLoading.value=false
//                if (response.isSuccessful && response.body() != null) {
//                    val result = response.body()
//                    if (result!!.status) {
//                        resultantSignIn.value= WebResponse(Status.SUCCESS, result, null)
//                    } else {
//                        resultantSignIn.value= WebResponse(Status.FAILURE, null, result.message)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<SignUpResponse>, error: Throwable) {
//                isLoading.value=false
////                 Log.e("onFailure", error.printStackTrace().toString())
//                val errorMsg: String? = ErrorHandler.reportError(error)
//                resultantSignIn.value= WebResponse(Status.FAILURE, null, errorMsg)
//            }
//        })
//
//    }


    @ExperimentalCoroutinesApi
    fun attemptSignIn()  {
        viewModelScope.launch {
            signInRepository.attemptSignIn(headerMap,signInRequest)
                .onStart {
                    /* emit loading state */
                    loadGeneral.value=true
                    isViewEnable.value=false
                }
                .onCompletion {
                    loadGeneral.value=false
                    isViewEnable.value=true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultantSignIn.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                    resultantSignIn.value= WebResponse(Status.SUCCESS, result, null)
                    else
                    resultantSignIn.value= WebResponse(Status.FAILURE, null, result.message)
                }


        }
    }



    @ExperimentalCoroutinesApi
    fun attemptSocialSignIn(signInRequest: SocialSignInRequest, signInType: String)  {
        viewModelScope.launch {
            signInRepository.attemptSocialSignIn(headerMap,signInRequest)
                .onStart {
                    /* emit loading state */
                    if(signInType.equals(SignInType.GOOGLE)) loadGoogle.value=true
                    if(signInType.equals(SignInType.FACEBOOK)) loadFacebook.value=true
                    isViewEnable.value=false
                }
                .onCompletion {
                    if(signInType.equals(SignInType.GOOGLE)) loadGoogle.value=false
                    if(signInType.equals(SignInType.FACEBOOK)) loadFacebook.value=false
                    isViewEnable.value=true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultantSignIn.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect { result->
                    if(result.status)
                        resultantSignIn.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantSignIn.value= WebResponse(Status.FAILURE, null, result.message)
                }


        }
    }

}
