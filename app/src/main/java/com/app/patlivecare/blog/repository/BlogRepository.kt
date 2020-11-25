package com.app.patlivecare.blog.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.app.patlivecare.blog.model.BlogInfo
import com.app.patlivecare.blog.model.BlogResponse
import com.app.patlivecare.network.WebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class BlogRepository(private var webService: WebService,private var headers: HashMap<String, String>): PageKeyedDataSource<Long, BlogInfo>() {
    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, BlogInfo>) {
        webService.fetchBlogs(headers,1).enqueue(object :
            Callback<BlogResponse> {
            override fun onResponse(call: Call<BlogResponse>, response: Response<BlogResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    if (result?.data?.blogList!=null)
                        callback.onResult(result.data.blogList,null,2)
                }
            }

            override fun onFailure(call: Call<BlogResponse>, error: Throwable) {}
        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, BlogInfo>) {
        webService.fetchBlogs(headers,params.key).enqueue(object :
            Callback<BlogResponse> {
            override fun onResponse(call: Call<BlogResponse>, response: Response<BlogResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    if (result?.data?.blogList!=null)
                        callback.onResult(result.data.blogList,params.key+1)
                }
            }

            override fun onFailure(call: Call<BlogResponse>, error: Throwable) {}
        })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, BlogInfo>) {}


    class BlogDataSourceFactory (val webService: WebService, var headers: HashMap<String, String>) : DataSource.Factory<Long, BlogInfo>() {
        val userLiveDataSource = MutableLiveData<BlogRepository>()

        override fun create(): DataSource<Long, BlogInfo> {
            val userDataSource = BlogRepository(webService,headers)
            userLiveDataSource.postValue(userDataSource)
            return userDataSource
        }




    }
}