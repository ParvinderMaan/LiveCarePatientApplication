package com.app.patlivecare.password.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.password.model.ForgotPasswordResponse
import com.app.patlivecare.password.repository.ForgotPasswordRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ForgotPasswordViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnabled: MutableLiveData<Boolean>
    var resultantForgotPassword: MutableLiveData<WebResponse<ForgotPasswordResponse>>
    var resultantResetPassword: MutableLiveData<WebResponse<GeneralResponse>>

    var isViewEnable: MutableLiveData<Boolean>
    var repository: ForgotPasswordRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnabled = MutableLiveData()
        resultantForgotPassword = MutableLiveData()
        resultantResetPassword= MutableLiveData()
        isViewEnable= MutableLiveData()
        repository= ForgotPasswordRepository(webService)
    }


    @ExperimentalCoroutinesApi
    fun attemptForgotPassword(email: String) {
        val json = JsonObject()
        json.addProperty("email", email)
        viewModelScope.launch {
            repository.attemptForgotPassword(json)
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
                    resultantForgotPassword.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantForgotPassword.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantForgotPassword.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }


    }


    @ExperimentalCoroutinesApi
    fun attemptResetPassword(email: String,otp: String,newPassword: String) {
        val json = JsonObject()
        json.addProperty("email", email)
        json.addProperty("otp", otp)
        json.addProperty("newPassword", newPassword)
        viewModelScope.launch {
            repository.attemptResetPassword(json)
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
                    resultantResetPassword.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantResetPassword.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantResetPassword.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }
    }
}