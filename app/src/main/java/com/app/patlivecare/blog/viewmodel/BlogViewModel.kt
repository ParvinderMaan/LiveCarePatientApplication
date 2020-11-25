package com.app.patlivecare.blog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.blog.model.BlogInfo
import com.app.patlivecare.blog.repository.BlogRepository
import com.app.patlivecare.network.WebService
import java.util.HashMap

class BlogViewModel(application: Application) : AndroidViewModel(application) {
    var headers: HashMap<String, String>
    private var webService: WebService
    var isLoading: MutableLiveData<Boolean>
    private var config: PagedList.Config
    var isListEmpty: MutableLiveData<Boolean>
    private lateinit var itemDataSourceFactory: BlogRepository.BlogDataSourceFactory
    private lateinit var liveDataSource: LiveData<BlogRepository>
    lateinit var userPagedList: LiveData<PagedList<BlogInfo>>

    init {
        webService = (application as LiveCareApplication).getWebServiceInstance()
        isLoading = MutableLiveData()
        isListEmpty= MutableLiveData()
        headers= HashMap()
        config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)
            .setPageSize(10)
            .build()
    }



    fun fetchBlogs() {
        itemDataSourceFactory = BlogRepository.BlogDataSourceFactory(webService,headers)
        liveDataSource = itemDataSourceFactory.userLiveDataSource
        userPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<BlogInfo>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    // Handle empty initial load here
                    isListEmpty.value=true
                    isLoading.value=false
                }

                override fun onItemAtEndLoaded(itemAtEnd: BlogInfo) {
                    super.onItemAtEndLoaded(itemAtEnd)
                    // Here you can listen to last item on list

                }

                override fun onItemAtFrontLoaded(itemAtFront: BlogInfo) {
                    super.onItemAtFrontLoaded(itemAtFront)
                    // Here you can listen to first item on list
                    isLoading.value=false
                }
            })
            .build()

    }







}