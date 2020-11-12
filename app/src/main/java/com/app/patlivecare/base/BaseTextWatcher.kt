package com.app.patlivecare.base

import android.text.Editable
import android.text.TextWatcher

/**
 * @author Pmaan on 4/6/18.
 * Copyright © All rights reserved.
 */
abstract class BaseTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {} // not used ....

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        onTextChanged(start, before, count, s)
    }

    override fun afterTextChanged(s: Editable) {} // not used ....
    abstract fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?)
}