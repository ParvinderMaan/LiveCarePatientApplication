package com.app.patlivecare.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.app.patlivecare.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment()  {

     fun showSnackBar(msg:String, @ColorRes colorId:Int= R.color.colorRed){
        val snackBar= Snackbar.make(getRootView(), msg, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(requireActivity(),colorId))
        snackBar.show()
    }

     abstract fun getRootView(): View


}

// extention functions
fun View.isUserInteractionEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup && this.childCount > 0) {
        this.children.forEach {
            it.isUserInteractionEnabled(enabled)
        }
    }
}