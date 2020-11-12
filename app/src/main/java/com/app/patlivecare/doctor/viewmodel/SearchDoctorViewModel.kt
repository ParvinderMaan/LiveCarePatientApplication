package com.app.patlivecare.doctor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.model.DoctorResponse
import com.app.patlivecare.doctor.repository.FindDoctorRepository
import com.app.patlivecare.doctor.repository.SearchDoctorRespository
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.HashMap

class SearchDoctorViewModel (application: Application) : AndroidViewModel(application) {
    var headers: HashMap<String, String>
    private var webService: WebService
    var specialityId=""
    var isLoading: MutableLiveData<Boolean>
    var isListEmpty: MutableLiveData<Boolean>
    private lateinit var repository: SearchDoctorRespository
    var lstOfDoctor: MutableLiveData<List<DoctorInfo>>
    var resultSearchDoctor: MutableLiveData<WebResponse<DoctorResponse>>

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isListEmpty = MutableLiveData()
        lstOfDoctor = MutableLiveData()
        resultSearchDoctor= MutableLiveData()
        headers = HashMap()
        repository=SearchDoctorRespository(webService)

    }


    @ExperimentalCoroutinesApi
    fun fetchSearchDoctor(queryText: String) {
        viewModelScope.launch {
            repository.fetchSearchDoctor(headers,queryText)
                .onStart {
                    /* emit loading state */
                    isLoading.value=true
                }
                .onCompletion {
                    isLoading.value=false
                }
                .catch { exception ->
                    /* emit error state */
                    val errorMsg: String? = ErrorHandler.reportError(exception)
                    resultSearchDoctor.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultSearchDoctor.value= WebResponse(Status.SUCCESS, it, null)
                    else resultSearchDoctor.value= WebResponse(Status.FAILURE, it, it.message)
                }
        }

    }

}