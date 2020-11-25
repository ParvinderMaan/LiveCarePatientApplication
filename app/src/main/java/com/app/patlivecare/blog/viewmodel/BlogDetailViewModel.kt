package com.app.patlivecare.blog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.blog.model.BlogDetailResponse
import com.app.patlivecare.blog.repository.BlogDetailRepository
import com.app.patlivecare.extra.WebResponse
import com.app.patlivecare.helper.ErrorHandler
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BlogDetailViewModel (application: Application) : AndroidViewModel(application) {
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    var resultBlogDetail: MutableLiveData<WebResponse<BlogDetailResponse>>
    var repository: BlogDetailRepository

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        resultBlogDetail= MutableLiveData()
       repository= BlogDetailRepository(webService)
    }

     @ExperimentalCoroutinesApi
     fun fetchBlogDetail(headers: HashMap<String, String>, id:String){

        viewModelScope.launch {

            repository.fetchBlogDetail(headers,id)
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
                    resultBlogDetail.value= WebResponse(Status.FAILURE, null, errorMsg)
                }
                .collect {
                    if(it.status)
                    resultBlogDetail.value= WebResponse(Status.SUCCESS, it, null)
                    else
                    resultBlogDetail.value= WebResponse(Status.FAILURE, null,it.message)

                }
        }
    }



}