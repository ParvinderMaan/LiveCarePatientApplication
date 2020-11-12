package com.app.patlivecare.medicalrecord.repository

import com.app.patlivecare.GeneralResponse
import com.app.patlivecare.medicalrecord.model.AllergyHistoryInfo
import com.app.patlivecare.medicalrecord.model.AllergyHistoryResponse
import com.app.patlivecare.medicalrecord.model.AttachmentHistoryResponse
import com.app.patlivecare.network.WebService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.HeaderMap
import retrofit2.http.Part

class AttachmentAndReportRepository(val webService: WebService) {

    @ExperimentalCoroutinesApi
    suspend fun addAttachmentHistory(
        token: Map<String, String>,
        reportName: RequestBody,
        date: RequestBody,
        desc: RequestBody,
        documentFile: MultipartBody.Part
    ): Flow<GeneralResponse> {
        return flow {
            val result =
                webService.addAttachmentHistory(token, reportName, date, desc, documentFile)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun deleteAttachmentHistory(
        headers: HashMap<String, String>,
        id: Int
    ): Flow<GeneralResponse> {
        return flow {
            val result = webService.deleteAttachmentHistory(headers, id)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }

    @ExperimentalCoroutinesApi
    suspend fun fetchAttachmentHistory(headers: HashMap<String, String>): Flow<AttachmentHistoryResponse> {
        return flow {
            val result = webService.fetchAttachmentHistory(headers)
            emit(result)
        }.flowOn(Dispatchers.IO)

    }
}