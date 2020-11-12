package com.app.patlivecare.medicalrecord.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.medicalrecord.model.SurgicalHistoryInfo
import com.app.patlivecare.medicalrecord.viewmodel.AddSurgicalHistoryViewModel
import com.app.patlivecare.network.WebHeader
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_add_surgical_history.btn_save
import kotlinx.android.synthetic.main.fragment_add_surgical_history.cl_root
import kotlinx.android.synthetic.main.fragment_add_surgical_history.edt_date
import kotlinx.android.synthetic.main.fragment_add_surgical_history.edt_doc_name
import kotlinx.android.synthetic.main.fragment_add_surgical_history.edt_notes
import kotlinx.android.synthetic.main.fragment_add_surgical_history.edt_treatment_name
import kotlinx.android.synthetic.main.fragment_add_surgical_history.ibtn_close
import kotlinx.android.synthetic.main.fragment_add_surgical_history.progress_bar
import kotlinx.android.synthetic.main.fragment_add_surgical_history.toolbar_main
import kotlinx.android.synthetic.main.fragment_add_surgical_history.tv_header_title
import kotlinx.coroutines.*
import java.util.*


class AddSurgicalHistoryFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener?=null
    private lateinit var viewModel: AddSurgicalHistoryViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private var surgicalHistoryInfo: SurgicalHistoryInfo? = null
    companion object {
        fun newInstance() = AddSurgicalHistoryFragment()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
        if(context is HomeFragmentSelectedListener) mFragmentListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(AddSurgicalHistoryViewModel::class.java)
        viewModel.headerMap=headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_surgical_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_add_surgical_history)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initListener()
        initObserver()


    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it){
                progress_bar?.show()
                btn_save?.visibility=View.INVISIBLE
            }
            else {
                progress_bar?.hide()
                btn_save?.visibility=View.VISIBLE
            }

        })

        viewModel.isViewEnable.observe(viewLifecycleOwner,Observer {
            if (it) view?.isUserInteractionEnabled(true)
            else view?.isUserInteractionEnabled(false)

        })

        viewModel.resultAddRecord.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS ->{
                    showSnackBar(it.data?.message.toString(),R.color.colorGreen)
                    mFragmentListener?.refreshUi(FragmentType.SURGICAL_HISTORY_FRAGMENT)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(1200)
                        withContext(Dispatchers.Main) {
                            mFragmentListener?.popTopMostFragment()
                        }
                    }
                }
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })
    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }

        btn_save?.setOnClickListener {
            if(isFormValid())
                surgicalHistoryInfo?.let { it1 -> viewModel.addSurgicalHistory(it1) }
        }

        edt_date?.setOnClickListener {
            showDatePicker()
        }
    }

    override fun getRootView(): View {
        return cl_root
    }

    private fun isFormValid(): Boolean {
        val treatmentName = edt_treatment_name.text.toString()
        val docName = edt_doc_name.text.toString()
        val dateOfTreatment = edt_date.text.toString()
        val notes = edt_notes.text.toString()

        if (TextUtils.isEmpty(treatmentName)
            || TextUtils.isEmpty(docName)
            || TextUtils.isEmpty(dateOfTreatment)
            || TextUtils.isEmpty(notes)){
            showSnackBar(getString(R.string.alert_please_complete_required_info))
            return false
        }
        surgicalHistoryInfo= SurgicalHistoryInfo()
        surgicalHistoryInfo?.treatmentName=treatmentName
        surgicalHistoryInfo?.doctorName=docName
        surgicalHistoryInfo?.date=dateOfTreatment
        surgicalHistoryInfo?.description=notes
        return true
    }

    /*     dd-MM-yyyy     */
    private fun showDatePicker() {
        val initYear: Int
        val initMonth: Int
        val initDay: Int
        val c = Calendar.getInstance()
        val selectedDob: String = edt_date?.text.toString()
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
            DatePickerDialog.OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val dayOfMonthh = String.format(Locale.getDefault(), "%02d", dayOfMonth)
                val monthOfYearr = String.format(Locale.getDefault(), "%02d", monthOfYear + 1)
                val x = dayOfMonthh + "-" + monthOfYearr + "-" + year
                edt_date?.setText(x)

            },
            initYear,
            initMonth,
            initDay
        )
        datePickerDialog.datePicker.maxDate = c.timeInMillis
        datePickerDialog.setOnShowListener {
            val greenColor = ContextCompat.getColor(activity as Context, R.color.colorGreen)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(greenColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(greenColor)
        }
        datePickerDialog.show()


    }
}