package com.app.patlivecare.miscellaneous.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.miscellaneous.repository.SettingRepository
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SettingViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>
    var isEmailChecked: Boolean?=false
    var isSmsChecked:Boolean=false
    var resultantSignOut: MutableLiveData<WebResponse<GeneralResponse>>
    var resultantSmsService: MutableLiveData<WebResponse<GeneralResponse>>
    var resultantEmailService: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: HashMap<String, String>
    var repository: SettingRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnable= MutableLiveData()
        headerMap=HashMap()
        resultantSignOut = MutableLiveData()
        resultantSmsService = MutableLiveData()
        resultantEmailService = MutableLiveData()
        repository=SettingRepository(webService)
    }

    fun attemptSignOut()  {
        viewModelScope.launch {
            repository.attemptSignOut(headerMap)
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
                    resultantSignOut.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantSignOut.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantSignOut.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }

    }


    @ExperimentalCoroutinesApi
    fun alterEmailNotificationService(headers: Map<String, String>, isEnable:Boolean)  {
        val json = JsonObject()
        json.addProperty("emailNotificationStatus", isEnable)
        viewModelScope.launch {
            repository.alterNotificationService(headers,json)
                .onStart {
                    /* emit loading state */
//                    isLoading.value=true
                    isViewEnable.value=false
                }
                .onCompletion {
//                    isLoading.value=false
                    isViewEnable.value=true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultantEmailService.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantEmailService.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantEmailService.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }

    }

    @ExperimentalCoroutinesApi
    fun alterSmsNotificationService(headers: Map<String, String>, isEnable:Boolean)  {
        val json = JsonObject()
        json.addProperty("smsNotificationStatus", isEnable)
        viewModelScope.launch {
            repository.alterNotificationService(headers,json)
                .onStart {
                    /* emit loading state */
//                    isLoading.value=true
                    isViewEnable.value=false
                }
                .onCompletion {
//                    isLoading.value=false
                    isViewEnable.value=true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultantSmsService.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantSmsService.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantSmsService.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }

    }

}
