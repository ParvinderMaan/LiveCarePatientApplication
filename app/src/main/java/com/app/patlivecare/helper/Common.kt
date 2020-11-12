package com.app.patlivecare.helper

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class Common {
    companion object{

        fun prepareImagePart(filie: String, name: String): MultipartBody.Part {
            val file = File(filie)
            val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            return MultipartBody.Part.Companion.createFormData(name,file.name,requestFile)
        }
        fun prepareFilePart(filie: String, name: String): MultipartBody.Part {
            val file = File(filie)
            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            return MultipartBody.Part.Companion.createFormData(name,file.name,requestFile)
        }
        fun prepareTextPart(name: String): RequestBody {
            return name.toRequestBody("text/plain".toMediaTypeOrNull())
        }
    }

}