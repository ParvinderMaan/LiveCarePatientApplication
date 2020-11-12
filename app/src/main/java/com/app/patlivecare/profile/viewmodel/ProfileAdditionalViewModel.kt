package com.app.patlivecare.profile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status.FAILURE
import com.app.patlivecare.annotation.Status.SUCCESS
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import com.app.patlivecare.profile.model.AdditionalInfoResponse
import com.app.patlivecare.profile.model.AdditionalProfileInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileAdditionalViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var isViewEnable: MutableLiveData<Boolean>
    var resultAdditionInfo: MutableLiveData<WebResponse<AdditionalInfoResponse>>
    var resultUpdateProfileInfo: MutableLiveData<WebResponse<GeneralResponse>>
    var headerMap:Map<String, String>
    var additionalProInfo:MutableLiveData<AdditionalProfileInfo>
    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isViewEnable= MutableLiveData()
        headerMap=HashMap()
        resultAdditionInfo = MutableLiveData()
        resultUpdateProfileInfo= MutableLiveData()
        additionalProInfo= MutableLiveData()
    }


    fun fetchAdditionalInfo() {
        this.isLoading.value = true
        webService.fetchAdditionalProfileInfo(headerMap)
            ?.enqueue(object : Callback<AdditionalInfoResponse?> {
                override fun onResponse(
                    call: Call<AdditionalInfoResponse?>,
                    response: Response<AdditionalInfoResponse?>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val result: AdditionalInfoResponse? = response.body()
                        if (result!!.status) {
                            resultAdditionInfo.value=WebResponse(SUCCESS, result, null)
                        } else {
                            resultAdditionInfo.value=WebResponse(FAILURE, null, result.message)
                        }
                    } else {
                        val errorMsg: String = ErrorHandler.reportError(response.code())
                        resultAdditionInfo.value=WebResponse(FAILURE, null, errorMsg)
                    }
                }

                override fun onFailure(call: Call<AdditionalInfoResponse?>, error: Throwable) {
                    isLoading.value = false
                    val errorMsg: String? = ErrorHandler.reportError(error)
                    resultAdditionInfo.value= WebResponse(FAILURE, null, errorMsg)
                }
            })
    }

    fun updateAdditionalProfileInfo() {
        isLoading.value=true
        this.isViewEnable.value=false
        webService.updateAdditionalProfileInfo(headerMap, additionalProInfo.value as AdditionalProfileInfo)?.enqueue(object : Callback<GeneralResponse?> {
                override fun onResponse(call: Call<GeneralResponse?>, response: Response<GeneralResponse?>) {
                    isLoading.value=false
                    isViewEnable.value=true
                    if (response.isSuccessful && response.body() != null) {
                        val result: GeneralResponse? = response.body()
                        if (result!!.status) {
                            resultUpdateProfileInfo.setValue(WebResponse(SUCCESS, result, null))
                        } else {
                            resultUpdateProfileInfo.setValue(
                                WebResponse(FAILURE, null, result.message))
                        }
                    } else {
                        val errorMsg = ErrorHandler.reportError(response.code())
                        resultUpdateProfileInfo.setValue(WebResponse(FAILURE, null, errorMsg))
                    }
                }

                override fun onFailure(
                    call: Call<GeneralResponse?>,
                    error: Throwable
                ) {
                    isLoading.value = false
                    isViewEnable.value = true
                    val errorMsg = ErrorHandler.reportError(error)
                    resultUpdateProfileInfo.setValue(WebResponse(FAILURE, null, errorMsg))
                }
            })
    }


}