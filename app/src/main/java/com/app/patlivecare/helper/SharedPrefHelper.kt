package com.app.patlivecare.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.base.SingletonHolder

class SharedPrefHelper private constructor(var application: LiveCareApplication) {
    private var sharedPref: SharedPreferences
    private var prefsEditor: Editor?=null

    init {
        sharedPref = application.getSharedPreferences(application.packageName, Activity.MODE_PRIVATE)

    }


    companion object : SingletonHolder<SharedPrefHelper, LiveCareApplication>(::SharedPrefHelper) {
        const val KEY_FIRST_NAME = "key_first_name"
        const val KEY_LAST_NAME = "key_last_name"
        const val KEY_GENDER = "key_gender"
        const val KEY_ID = "key_id"
        const val KEY_EMAIL = "key_email"
        const val KEY_PHONE = "key_phone"
        const val KEY_PROFILE_PIC = "key_pro_pic"
        const val KEY_ADDRESS = "key_address"
        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_DEVICE_ID = "key_device_id"
        const val KEY_SMS_NOTIFICATION = "key_sms_notification"
        const val KEY_EMAIL_NOTIFICATION = "key_email_notification"
        const val KEY_IS_SIGN_IN = "key_sign_in"
        const val KEY_IS_SOCIAL_SIGN_IN = "key_social_sign_in"
        const val KEY_PROFILE_PERCENT = "key_profile_percent"

    }

    fun read(key: String?, defValue: String?): String? {
        return sharedPref.getString(key, defValue)
    }

    fun write(key: String, value: String): SharedPrefHelper {
        prefsEditor?.putString(key, value)
        return this
    }

    @SuppressLint("CommitPrefEdits")
    fun builder(): SharedPrefHelper {
        prefsEditor=sharedPref.edit()
        return this
    }


    fun read(key: String, defValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defValue)
    }

    fun write(key: String, value: Boolean): SharedPrefHelper {
        prefsEditor?.putBoolean(key, value)
        return this
    }

    fun read(key: String?, defValue: Int): Int? {
        return sharedPref.getInt(key, defValue)
    }

    fun write(key: String?, value: Int): SharedPrefHelper {
        prefsEditor?.putInt(key, value)
        return this
    }

    fun clear() {
        val prefsEditor: Editor = sharedPref.edit()
        prefsEditor.clear()
        prefsEditor.apply()
    }

    fun build(){
        prefsEditor?.apply()
    }



}