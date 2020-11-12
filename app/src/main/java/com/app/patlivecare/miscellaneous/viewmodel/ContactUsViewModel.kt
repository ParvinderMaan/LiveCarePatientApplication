package com.app.patlivecare.miscellaneous.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.miscellaneous.model.ContactUsRequest
import com.app.patlivecare.miscellaneous.repository.ContactUsRepository
import com.app.patlivecare.miscellaneous.repository.SettingRepository
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ContactUsViewModel  (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>
    var resultContactUs: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: HashMap<String, String>
    var repository: ContactUsRepository
    var contactUsRequest:ContactUsRequest

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnable = MutableLiveData()
        headerMap = HashMap()
        contactUsRequest=ContactUsRequest()
        resultContactUs = MutableLiveData()
        repository = ContactUsRepository(webService)
    }

    fun attemptContactUs() {
        viewModelScope.launch {
            repository.attemptContactUs(headerMap,contactUsRequest)
                .onStart {
                    /* emit loading state */
                    isLoading.value = true
                    isViewEnable.value = false
                }
                .onCompletion {
                    isLoading.value = false
                    isViewEnable.value = true
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultContactUs.value = WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect { result ->
                    if (result.status)
                        resultContactUs.value = WebResponse(Status.SUCCESS, result, null)
                    else
                        resultContactUs.value = WebResponse(Status.FAILURE, null, result.message)
                }
        }

    }

}