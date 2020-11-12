package com.app.patlivecare.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.app.patlivecare.R

class AppointmentRequestDialogFragment : DialogFragment() {
    private var dialogListener: DialogListener? = null

    companion object {
        fun newInstance(): AppointmentRequestDialogFragment {
            return AppointmentRequestDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_appointment_request))
        builder.setMessage(resources.getString(R.string.title_request_appointment))
        builder.setPositiveButton(resources.getString(R.string.title_yes)) { dialog: DialogInterface?, which: Int ->
            dialogListener?.onClickYes()
        }
        builder.setNegativeButton(resources.getString(R.string.title_no)) { dialog: DialogInterface?, which: Int ->
            dialogListener?.onClickNo()
        }
        return builder.create()
    }


    fun setOnDialogListener(signOutDialogListener: DialogListener) {
        this.dialogListener = signOutDialogListener
    }

    interface  DialogListener {
        fun onClickYes()
        fun onClickNo()
    }
}