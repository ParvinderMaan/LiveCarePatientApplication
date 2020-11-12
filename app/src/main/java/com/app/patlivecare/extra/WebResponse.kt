package com.app.patlivecare.extra


class WebResponse<T>(status: Int, data: T?, errorMsg: String?) {
    var status: Int
    var data: T?
    var errorMsg: String?=null

    init {
        this.status = status
        this.data = data
        this.errorMsg = errorMsg
    }
}