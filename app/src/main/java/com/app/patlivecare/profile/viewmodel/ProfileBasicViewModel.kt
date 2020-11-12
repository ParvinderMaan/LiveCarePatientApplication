package com.app.patlivecare.profile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.profile.model.BasicInfoResponse
import com.app.patlivecare.profile.model.BasicProfileRequest
import com.app.patlivecare.profile.model.CountryInfoResponse
import com.app.patlivecare.profile.model.StateInfoResponse
import com.app.patlivecare.profile.repository.ProfileBasicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileBasicViewModel(application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultBasicInfo: MutableLiveData<WebResponse<BasicInfoResponse>>
    var resultUpdateBasicInfo: MutableLiveData<WebResponse<GeneralResponse>>
    var resultCountries: MutableLiveData<WebResponse<CountryInfoResponse>>
    var resultStates: MutableLiveData<WebResponse<StateInfoResponse>>
    var basicProRequest:BasicProfileRequest
    var headerMap: Map<String, String>
    var isViewEnable: MutableLiveData<Boolean>
    var basicInfoData:MutableLiveData<BasicInfoResponse.Data>
    var lstOfCountries:MutableLiveData<List<CountryInfoResponse.Country>>
    var lstOfStates:MutableLiveData<List<StateInfoResponse.State>>
    var repository:ProfileBasicRepository
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        headerMap = HashMap()
        resultBasicInfo = MutableLiveData()
        resultUpdateBasicInfo = MutableLiveData()
        resultCountries= MutableLiveData()
        basicProRequest = BasicProfileRequest()
        isViewEnable= MutableLiveData()
        basicInfoData= MutableLiveData()
        resultStates= MutableLiveData()
        lstOfCountries= MutableLiveData()
        lstOfStates= MutableLiveData()
        repository= ProfileBasicRepository(webService)
    }


//    fun fetchBasicInfo() {
//        this.isLoading.value = true
//        webService.fetchBasicProfileInfo(headerMap)
//            ?.enqueue(object : Callback<BasicInfoResponse?> {
//                override fun onResponse(
//                    call: Call<BasicInfoResponse?>,
//                    response: Response<BasicInfoResponse?>
//                ) {
//                    isLoading.value = false
//                    if (response.isSuccessful && response.body() != null) {
//                        val result: BasicInfoResponse? = response.body()
//                        if (result!!.status) {
//                            resultBasicInfo.value = WebResponse(Status.SUCCESS, result, null)
//                        } else {
//                            resultBasicInfo.value = WebResponse(Status.FAILURE, null, result.message)
//                        }
//                    } else {
//                        val errorMsg: String = ErrorHandler.reportError(response.code())
//                        resultBasicInfo.value = WebResponse(Status.FAILURE, null, errorMsg)
//                    }
//                }
//
//                override fun onFailure(call: Call<BasicInfoResponse?>, error: Throwable) {
//                    isLoading.value = false
//                    val errorMsg: String? = ErrorHandler.reportError(error)
//                    resultBasicInfo.value = WebResponse(Status.FAILURE, null, errorMsg)
//                }
//            })
//    }

//    fun fetchCountries() {
//        webService.fetchCountries()?.enqueue(object : Callback<CountryInfoResponse?> {
//                override fun onResponse(call: Call<CountryInfoResponse?>, response: Response<CountryInfoResponse?>) {
//                    if (response.isSuccessful && response.body() != null) {
//                        val result: CountryInfoResponse? = response.body()
//                        if (result!!.status) {
//                            resultCountries.value = WebResponse(Status.SUCCESS, result, null)
//                        } else {
//                            resultCountries.value = WebResponse(Status.FAILURE, null, result.message)
//                        }
//                    } else {
//                        val errorMsg: String = ErrorHandler.reportError(response.code())
//                        resultCountries.value = WebResponse(Status.FAILURE, null, errorMsg)
//                    }
//                }
//
//                override fun onFailure(call: Call<CountryInfoResponse?>, error: Throwable) {
//                    val errorMsg: String? = ErrorHandler.reportError(error)
//                    resultCountries.value = WebResponse(Status.FAILURE, null, errorMsg)
//                }
//            })
//    }

    @ExperimentalCoroutinesApi
    fun fetchCountries() {
        viewModelScope.launch {

            repository.fetchCountries()
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
                    resultCountries.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    resultCountries.value= WebResponse(Status.SUCCESS, it, null)
                }
        }
    }

//    fun fetchState(countryId:Int) {
//        webService.fetchStates(countryId)?.enqueue(object : Callback<StateInfoResponse?> {
//            override fun onResponse(call: Call<StateInfoResponse?>, response: Response<StateInfoResponse?>) {
//                if (response.isSuccessful && response.body() != null) {
//                    val result: StateInfoResponse? = response.body()
//                    if (result!!.status) {
//                        resultStates.value = WebResponse(Status.SUCCESS, result, null)
//                    } else {
//                        resultStates.value = WebResponse(Status.FAILURE, null, result.message)
//                    }
//                } else {
//                    val errorMsg: String = ErrorHandler.reportError(response.code())
//                    resultStates.value = WebResponse(Status.FAILURE, null, errorMsg)
//                }
//            }
//
//            override fun onFailure(call: Call<StateInfoResponse?>, error: Throwable) {
//                val errorMsg: String? = ErrorHandler.reportError(error)
//                resultStates.value = WebResponse(Status.FAILURE, null, errorMsg)
//            }
//        })
//    }


//    fun updateBasicProfileInfo() {
//        isLoading.value=true
//        isViewEnable.value=false
//        webService.updateBasicProfileInfo(headerMap, basicProRequest)?.enqueue(object : Callback<GeneralResponse?> {
//            override fun onResponse(call: Call<GeneralResponse?>, response: Response<GeneralResponse?>) {
//                isLoading.value=false
//                isViewEnable.value=true
//                if (response.isSuccessful && response.body() != null) {
//                    val result: GeneralResponse? = response.body()
//                    if (result!!.status) {
//                        resultUpdateBasicInfo.setValue(WebResponse(Status.SUCCESS, result, null))
//                    } else {
//                        resultUpdateBasicInfo.setValue(
//                            WebResponse(Status.FAILURE, null, result.message))
//                    }
//                } else {
//                    val errorMsg = ErrorHandler.reportError(response.code())
//                    resultUpdateBasicInfo.setValue(WebResponse(Status.FAILURE, null, errorMsg))
//                }
//            }
//
//            override fun onFailure(
//                call: Call<GeneralResponse?>,
//                error: Throwable
//            ) {
//                isLoading.value = false
//                isViewEnable.value = true
//                val errorMsg = ErrorHandler.reportError(error)
//                resultUpdateBasicInfo.setValue(WebResponse(Status.FAILURE, null, errorMsg))
//            }
//        })
//    }


    @ExperimentalCoroutinesApi
    fun fetchState(countryId:Int) {
        viewModelScope.launch {

            repository.fetchStates(countryId)
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
                    resultStates.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    resultStates.value= WebResponse(Status.SUCCESS, it, null)
                }
        }
    }

    @ExperimentalCoroutinesApi
    fun fetchBasicInfo() {
        viewModelScope.launch {

            repository.fetchBasicProfileInfo(headerMap)
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
                    resultBasicInfo.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    resultBasicInfo.value= WebResponse(Status.SUCCESS, it, null)
                }
        }

    }

    @ExperimentalCoroutinesApi
    fun updateBasicProfileInfo(){
        viewModelScope.launch {
            repository.updateBasicProfileInfo(headerMap,basicProRequest)
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
                    resultUpdateBasicInfo.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)resultUpdateBasicInfo.value= WebResponse(Status.SUCCESS, it, null)
                    else resultUpdateBasicInfo.value= WebResponse(Status.FAILURE, null, it.message)
                }
        }
    }

//    @ExperimentalCoroutinesApi
//    fun fetchBasicInfoAndCountries() {
//        viewModelScope.launch {
//            val flow1 = profileBasicRepository.fetchBasicProfileInfo(headerMap)
//            val flow2= profileBasicRepository.fetchCountries()
//            flow1.zip(flow2) {
//                    firstResponse, secondResponse ->
//                if (firstResponse.status && secondResponse.status) {
//
//                       }
//        }
//    }
}