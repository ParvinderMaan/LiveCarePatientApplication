package com.app.patlivecare.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.app.patlivecare.R

class ConfirmAppointmentDialogFragment : DialogFragment() {
    private var message: String?=null
    private var dialogListener: DialogListener? = null

    companion object {
        fun newInstance(payload: Any?): ConfirmAppointmentDialogFragment {
                val fragment = ConfirmAppointmentDialogFragment()
                val bundle = Bundle()
                if (payload is String) bundle.putString("KEY", payload)
                fragment.arguments = bundle
                return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString("KEY")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_appointment_request))
        builder.setMessage(message)
        builder.setPositiveButton(
            resources.getString(R.string.title_ok)
        ) { dialog: DialogInterface?, which: Int ->
            dialogListener?.onClickYes()
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