package com.app.patlivecare.miscellaneous.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.miscellaneous.model.AppInfoResponse
import com.app.patlivecare.miscellaneous.repository.AboutUsRepository
import com.app.patlivecare.miscellaneous.repository.PrivacyPolicyRepository
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PrivacyPolicyViewModel  (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultPrivacyPolicy: MutableLiveData<WebResponse<AppInfoResponse>>
    var repository: PrivacyPolicyRepository

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        resultPrivacyPolicy= MutableLiveData()
        repository = PrivacyPolicyRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun fetchAppInfo() {
        viewModelScope.launch {
            repository.fetchAppInfo()
                .onStart {
                    /* emit loading state */
                    isLoading.value = true
                }
                .onCompletion {
                    isLoading.value = false
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultPrivacyPolicy.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect { result ->
                    if (result.status)
                        resultPrivacyPolicy.value = WebResponse(Status.SUCCESS, result, null)
                    else
                        resultPrivacyPolicy.value = WebResponse(Status.FAILURE, null, result.message)
                }
        }

    }

}