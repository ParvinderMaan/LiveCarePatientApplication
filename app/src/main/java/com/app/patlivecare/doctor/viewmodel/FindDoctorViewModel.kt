package com.app.patlivecare.doctor.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.repository.FindDoctorRepository
import com.app.patlivecare.network.WebService

import java.util.HashMap

class FindDoctorViewModel (application: Application) : AndroidViewModel(application) {
    var headers: HashMap<String, String>
    private var webService: WebService
    var specialityId=""
    var queryText:String=""
    var isLoading: MutableLiveData<Boolean>
    private var config: PagedList.Config
    var isListEmpty: MutableLiveData<Boolean>
    private lateinit var itemDataSourceFactory: FindDoctorRepository.FindDoctorDataSourceFactory
    private lateinit var liveDataSource: LiveData<FindDoctorRepository>
    lateinit var userPagedList: LiveData<PagedList<DoctorInfo>>
    var lstOfDoctor: LiveData<List<DoctorInfo>>

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isListEmpty = MutableLiveData()
        lstOfDoctor = MutableLiveData()
        headers = HashMap()
        config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)
            .setPageSize(10)
            .build()





    }



    fun fetchDoctors() {
        itemDataSourceFactory = FindDoctorRepository.FindDoctorDataSourceFactory(webService, headers,specialityId,queryText)
        liveDataSource = itemDataSourceFactory.userLiveDataSource
        userPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<DoctorInfo>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    // Handle empty initial load here
                    isListEmpty.value = true
                    isLoading.value = false
                }
                override fun onItemAtEndLoaded(itemAtEnd: DoctorInfo) {
                    super.onItemAtEndLoaded(itemAtEnd)
                    // Here you can listen to last item on list
                }
                override fun onItemAtFrontLoaded(itemAtFront: DoctorInfo) {
                    super.onItemAtFrontLoaded(itemAtFront)
                    // Here you can listen to first item on list
                    isLoading.value = false
                }
            })
            .build()


    }

}