package com.app.patlivecare.helper

import android.util.Patterns
import java.util.regex.Pattern

object Validator {
    // check email validation
    fun isEmailValid(email: String?): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    // check alphanumeric validation
    fun isAlphaNumeric(password: String?): Boolean {
        val regex = "^[a-zA-Z0-9]+$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(password)
        return !matcher.matches()
    }
    // check uppercase and lower case validation
    fun isUpperAndLowerCase(input: String): Boolean {
        var upperCasePresent=false
        var lowerCasePresent=false
        for (i in 0 until input.length) {
            val currentChar = input.get(i)
            if (Character.isUpperCase(currentChar)) {
                upperCasePresent = true
            } else if (Character.isLowerCase(currentChar)) {
                lowerCasePresent = true
            }
        }
      return upperCasePresent && lowerCasePresent
    }

    fun isDigitAndSymbolCase(input: String): Boolean {
        val specialChars = "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?"
        var digitPresent=false
        var symbolPresent=false
        for (i in input.indices) {
            val currentChar = input[i]
            if (Character.isDigit(currentChar)) {
                digitPresent = true
            } else if (specialChars.contains(currentChar)) {
                symbolPresent = true
            }
        }
        return digitPresent && symbolPresent
    }

}