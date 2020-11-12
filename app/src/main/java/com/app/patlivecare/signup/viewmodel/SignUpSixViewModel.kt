package com.app.patlivecare.signup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.signup.model.SignUpRequest
import com.app.patlivecare.signup.model.SignUpResponse
import com.app.patlivecare.signup.repository.SignUpRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SignUpSixViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>
    var signUpRequest: SignUpRequest
    var resultantSignUp: MutableLiveData<WebResponse<SignUpResponse>>
    var headerMap:Map<String, String>
    var signUpRepository: SignUpRepository

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnable= MutableLiveData()
        headerMap=HashMap()
        resultantSignUp = MutableLiveData()
        signUpRequest= SignUpRequest()
        signUpRepository = SignUpRepository(webService)
    }

//    fun attemptSignUp()  {
//        isLoading.value=true
//        isViewEnable.value=false
//        webService.attemptSignUp(signUpRequest)?.enqueue(object : Callback<SignUpResponse> {
//            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
//                isLoading.value=false
//                isViewEnable.value=true
//                if (response.isSuccessful && response.body() != null) {
//                    val result = response.body()
//                    if (result!!.status) {
//                        resultantSignUp.value= WebResponse(Status.SUCCESS, result, null)
//                    } else {
//                        resultantSignUp.value= WebResponse(Status.FAILURE, null, result.message)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<SignUpResponse>, error: Throwable) {
//                isLoading.value=false
//                isViewEnable.value=true
////                 Log.e("onFailure", error.printStackTrace().toString())
//                val errorMsg: String? = ErrorHandler.reportError(error)
//                resultantSignUp.value= WebResponse(Status.FAILURE, null, errorMsg)
//            }
//        })
//
//    }

    @ExperimentalCoroutinesApi
    fun attemptSignUp() {
        viewModelScope.launch {

            signUpRepository.attemptSignUp(signUpRequest)
                .onStart {
                    /* emit loading state */
                    isLoading.value=true
                    isViewEnable.value=false
                }
                .onCompletion {
                    isLoading.value=false
                    isViewEnable.value=true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultantSignUp.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantSignUp.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantSignUp.value= WebResponse(Status.FAILURE, null, result.message)
                }

        }
    }
}
