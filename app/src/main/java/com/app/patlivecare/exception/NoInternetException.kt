package com.app.patlivecare.exception

import java.io.IOException
class NoInternetException  : IOException()  {

    override val message: String
        get() = "No Internet Connection"


}