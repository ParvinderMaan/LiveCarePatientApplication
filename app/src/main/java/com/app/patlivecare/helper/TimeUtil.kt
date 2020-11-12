package com.app.patlivecare.helper

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimeUtil private constructor() {

    companion object {
        // Method to utc to local
        fun utcToLocal(datesToConvert: String): String {
            var ourDate:String
            try {
                val formatter = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value = formatter.parse(datesToConvert)
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()) //this format changeable
                dateFormatter.timeZone = TimeZone.getDefault()
                 ourDate = dateFormatter.format(value)
                //Log.d("ourDate", ourDate);
            } catch (e: ParseException) {
                ourDate = "00-00-0000"
            }
        return ourDate
        }

        // Method to get days hours minutes seconds from milliseconds
         fun getTimeSpan(millisUntilFinished:Long):String{
            var millisUntilFinished:Long = millisUntilFinished
            val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
            millisUntilFinished -= TimeUnit.DAYS.toMillis(days)
            
            val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
            millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

            val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
            millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

            val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

            if(days>=1){
                // Format the string
                return String.format(Locale.getDefault(), "%02d days", days)
            }
            
            // Format the string
            return String.format(Locale.getDefault(), "%02d hour %02d min %02d sec", hours, minutes,seconds)
        }

    }
}