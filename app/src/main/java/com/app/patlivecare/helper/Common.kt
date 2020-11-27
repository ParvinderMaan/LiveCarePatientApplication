package com.app.patlivecare.helper

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

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

        fun printKeyHash(packageManager:PackageManager) {
            // Add code to print out the key hash
            try {
                val info: PackageInfo = packageManager.getPackageInfo("com.app.patlivecare", PackageManager.GET_SIGNATURES)
                for (signature in info.signatures) {
                    val md: MessageDigest = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e("KeyHash:", e.toString())
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash:", e.toString())
            }
        }

    }

}