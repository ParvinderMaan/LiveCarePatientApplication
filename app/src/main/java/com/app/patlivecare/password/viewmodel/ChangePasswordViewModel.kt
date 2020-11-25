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
import com.app.patlivecare.password.repository.ChangePasswordRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ChangePasswordViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnabled: MutableLiveData<Boolean>
    var resultantChangePassword: MutableLiveData<WebResponse<GeneralResponse>>
    var isViewEnable: MutableLiveData<Boolean>
    var repository: ChangePasswordRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnabled = MutableLiveData()
        resultantChangePassword = MutableLiveData()
        isViewEnable= MutableLiveData()
        repository= ChangePasswordRepository(webService)
    }


    @ExperimentalCoroutinesApi
    fun attemptChangePassword(headers: HashMap<String, String>,oldPassword: String,newPassword: String) {
        val json = JsonObject()
        json.addProperty("oldPassword", oldPassword)
        json.addProperty("newPassword", newPassword)
        viewModelScope.launch {
            repository.attemptChangePassword(headers,json)
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
                    resultantChangePassword.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {result->
                    if(result.status)
                        resultantChangePassword.value= WebResponse(Status.SUCCESS, result, null)
                    else
                        resultantChangePassword.value= WebResponse(Status.FAILURE, null, result.message)
                }
        }
    }




}