package com.app.patlivecare.medicalrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.medicalrecord.model.FamilyHistoryInfo
import com.app.patlivecare.medicalrecord.model.SurgicalHistoryInfo
import com.app.patlivecare.medicalrecord.repository.FamilyHistoryRepository
import com.app.patlivecare.medicalrecord.repository.SurgicalHistoryRepository
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AddFamilyHistoryViewModel  (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>
    var resultAddRecord: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: HashMap<String, String>
    var repository: FamilyHistoryRepository

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnable = MutableLiveData()
        headerMap = HashMap()
        resultAddRecord= MutableLiveData()
        repository = FamilyHistoryRepository(webService)
    }

    @ExperimentalCoroutinesApi
    fun addFamilyHistory(inn: FamilyHistoryInfo) {
        viewModelScope.launch {
            repository.addFamilyHistory(headerMap,inn)
                .onStart {
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
                    resultAddRecord.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultAddRecord.value= WebResponse(Status.SUCCESS, it, null)
                    else resultAddRecord.value= WebResponse(Status.FAILURE, null, it.message)
                }
        }

    }
}