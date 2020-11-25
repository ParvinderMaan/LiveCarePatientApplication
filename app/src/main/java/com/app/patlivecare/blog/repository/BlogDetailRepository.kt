package com.app.patlivecare.blog.repository

import com.app.patlivecare.blog.model.BlogDetailResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BlogDetailRepository (var webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun fetchBlogDetail(headers: HashMap<String, String>, id:String): Flow<BlogDetailResponse> {
        return flow {
            val result = webService.fetchBlogDetail(headers,id)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}