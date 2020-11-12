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
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryInfo
import com.app.patlivecare.medicalrecord.model.PastMedicalHistoryResponse
import com.app.patlivecare.medicalrecord.repository.PastMedicalHistoryRepository
import com.app.patlivecare.network.WebService
import com.app.patlivecare.profile.model.CountryInfoResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PastMedicalHistoryViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultFetchRecords: MutableLiveData<WebResponse<PastMedicalHistoryResponse>>
    var resultDeleteRecord: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap: HashMap<String, String>
    var lstOfMedicalRecord: MutableLiveData<MutableList<PastMedicalHistoryInfo>>
    var repository: PastMedicalHistoryRepository
     var itemToBeDeleted: Int=100000

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultFetchRecords = MutableLiveData()
        resultDeleteRecord= MutableLiveData()
        lstOfMedicalRecord = MutableLiveData()
        repository = PastMedicalHistoryRepository(webService)
    }


    @ExperimentalCoroutinesApi
    fun fetchPastMedicalHistory() {
        viewModelScope.launch {
            repository.fetchPastMedicalHistory(headerMap)
                .onStart {
                    isLoading.value=true
                }
                .onCompletion {
                    isLoading.value=false
                }
                .catch { exception ->
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultFetchRecords.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultFetchRecords.value= WebResponse(Status.SUCCESS, it, null)
                    else resultFetchRecords.value= WebResponse(Status.FAILURE, null, it.message)
                }
        }

    }

    @ExperimentalCoroutinesApi
    fun deletePastMedicalHistory(id: Int) {
        viewModelScope.launch {
            repository.deletePastMedicalHistory(headerMap,id)
                .onStart {
//                    isLoading.value=true
                }
                .onCompletion {
//                    isLoading.value=false
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultDeleteRecord.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultDeleteRecord.value= WebResponse(Status.SUCCESS, it, null)
                    else resultDeleteRecord.value= WebResponse(Status.FAILURE, null, it.message)
                }
        }

    }
}