package com.app.patlivecare.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.app.patlivecare.R

class SignOutDialogFragment : DialogFragment() {
    private var signOutDialogListener: SignOutDialogListener? = null

    companion object {
        fun newInstance(): SignOutDialogFragment {
            return SignOutDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.title_logout_msg))
        builder.setPositiveButton(
            resources.getString(R.string.title_yes)
        ) { dialog: DialogInterface?, which: Int ->
            signOutDialogListener?.onClickYes()

        }
        builder.setNegativeButton(
            resources.getString(R.string.title_no)
        ) { dialog: DialogInterface?, which: Int ->
            signOutDialogListener?.onClickNo()

        }
        return builder.create()
    }


    fun setOnSignOutDialogListener(signOutDialogListener: SignOutDialogListener) {
        this.signOutDialogListener = signOutDialogListener
    }

    interface  SignOutDialogListener {
        fun onClickYes()
        fun onClickNo()
    }
}