package com.app.patlivecare.medicalrecord.repository

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.medicalrecord.model.AttachmentHistoryResponse
import com.app.patlivecare.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody

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