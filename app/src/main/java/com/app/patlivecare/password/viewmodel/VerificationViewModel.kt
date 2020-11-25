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
import com.app.patlivecare.password.model.SendOtpResponse
import com.app.patlivecare.password.repository.VerificationRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class VerificationViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnabled: MutableLiveData<Boolean>
    var resultantSendOtpAtEmail: MutableLiveData<WebResponse<SendOtpResponse>>
    var resultantSendOtpAtPhone: MutableLiveData<WebResponse<GeneralResponse>>
    var resultantVerifyEmail: MutableLiveData<WebResponse<GeneralResponse>>
    var resultantVerifyPhone: MutableLiveData<WebResponse<GeneralResponse>>

    var isViewEnable: MutableLiveData<Boolean>
    var repository: VerificationRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnabled = MutableLiveData()
        resultantSendOtpAtEmail = MutableLiveData()
        resultantSendOtpAtPhone= MutableLiveData()
        resultantVerifyEmail = MutableLiveData()
        resultantVerifyPhone= MutableLiveData()
        isViewEnable= MutableLiveData()
        repository=VerificationRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun attemptVerifyEmail(
        headers: HashMap<String, String>,
        email: String,
        otp: String
    ) {
        val json = JsonObject()
        json.addProperty("email", email)
        json.addProperty("otp", otp)
        viewModelScope.launch {
            repository.attemptVerifyEmail(headers,json)
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
                    resultantVerifyEmail.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantVerifyEmail.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantVerifyEmail.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }
    }

    @ExperimentalCoroutinesApi
    fun attemptSendOtpAtEmail(email: String) {
        val json = JsonObject()
        json.addProperty("email", email)
        viewModelScope.launch {
            repository.attemptSendOtpAtEmail(json)
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
                    resultantSendOtpAtEmail.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantSendOtpAtEmail.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantSendOtpAtEmail.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }
    }



    @ExperimentalCoroutinesApi
    fun attemptSendOtpAtPhone(headerMap: Map<String, String>,phoneNo: String) {
        val phone = phoneNo.split("|");
        val json = JsonObject()
        json.addProperty("dialCode", phone[0])
        json.addProperty("phoneNo", phone[1])
        viewModelScope.launch {
            repository.attemptSendOtpAtPhone(headerMap,json)
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
                    resultantSendOtpAtPhone.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantSendOtpAtPhone.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantSendOtpAtPhone.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }
    }



    @ExperimentalCoroutinesApi
    fun attemptVerifyPhone(
        headers: HashMap<String, String>,
        otp: String
    ) {
        val json = JsonObject()
        json.addProperty("code", otp)
        viewModelScope.launch {
            repository.attemptVerifyPhone(headers,json)
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
                    resultantVerifyPhone.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantVerifyPhone.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantVerifyPhone.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }
    }
}