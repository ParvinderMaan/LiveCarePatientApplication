package com.app.patlivecare.network

class WebHeader private constructor() {

    companion object {
        // headers
        const val KEY_CONTENT_TYPE = "Content-Type"
        const val KEY_ACCEPT = "Accept"
        const val KEY_AUTHORIZATION = "Authorization"

        const val VAL_CONTENT_TYPE = "application/json"
        const val VAL_ACCEPT = "application/x-www-form-urlencoded"

    }

}
