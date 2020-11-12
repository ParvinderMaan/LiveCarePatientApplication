package com.app.patlivecare.signup.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.app.patlivecare.R
import kotlinx.android.synthetic.main.fragment_sign_up_two.*
import kotlinx.android.synthetic.main.fragment_sign_up_two.btn_back
import kotlinx.android.synthetic.main.fragment_sign_up_two.btn_continue
import java.util.*


class SignUpTwoFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpTwoFragment()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edt_dob?.setOnClickListener { showDatePicker() }
        btn_continue?.setOnClickListener {
         process()
        }
        btn_back?.setOnClickListener {
            (parentFragment as SignUpFragment).showPreviousFragment()
        }

        edt_dob?.setOnEditorActionListener { v, actionId, event ->
                if (actionId== EditorInfo.IME_ACTION_GO) {
                    process()
                }
                true
            }


    }

    private fun process() {
        val dob =  edt_dob?.text.toString()

        if(dob.isNotEmpty()){
            (parentFragment as SignUpFragment).signUpRequest?.dateOfBirth=dob
            (parentFragment as SignUpFragment).showNextFragment()
        }
        else  edt_dob?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, R.drawable.ic_tick_red, 0)



    }

    /*     dd-MM-yyyy     */
    private fun showDatePicker() {
        val initYear: Int
        val initMonth: Int
        val initDay: Int
        val c = Calendar.getInstance()
        val selectedDob: String = edt_dob?.text.toString()
        if (TextUtils.isEmpty(selectedDob)) {
            initYear = c[Calendar.YEAR]
            initMonth = c[Calendar.MONTH]
            initDay = c[Calendar.DAY_OF_MONTH]
        } else {
            val userSelected = selectedDob.split("-".toRegex()).toTypedArray()
            initYear = userSelected[2].toInt()
            initDay = userSelected[0].toInt()
            initMonth = userSelected[1].toInt() - 1
        }
        val datePickerDialog: DatePickerDialog
        datePickerDialog = DatePickerDialog(requireActivity(), R.style.PickerStyle,
            OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val dayOfMonthh = String.format(Locale.getDefault(), "%02d", dayOfMonth)
                val monthOfYearr= String.format(Locale.getDefault(), "%02d", monthOfYear+1)
                val x = dayOfMonthh + "-" + monthOfYearr + "-" + year
                edt_dob?.setText(x)
                edt_dob?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, R.drawable.ic_tick_green, 0)
            },
            initYear,
            initMonth,
            initDay
        )
        datePickerDialog.datePicker.maxDate = c.timeInMillis - 568025136000L // -18yrs
        datePickerDialog.setOnShowListener {
            val greenColor = ContextCompat.getColor(activity as Context, R.color.colorGreen)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(greenColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(greenColor)
        }
        datePickerDialog.show()


    }



}