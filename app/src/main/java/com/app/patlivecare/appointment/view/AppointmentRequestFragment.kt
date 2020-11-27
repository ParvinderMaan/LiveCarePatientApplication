package com.app.patlivecare.appointment.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.appointment.viewmodel.AppointmentRequestViewModel
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.dialog.AppointmentRequestDialogFragment
import com.app.patlivecare.dialog.ConfirmAppointmentDialogFragment
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_appointment_request.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class AppointmentRequestFragment  : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private lateinit var viewModel: AppointmentRequestViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private var doctorInfo: DoctorInfo? = null
    private var snackBarSuccess:Snackbar?=null
    companion object {
        fun newInstance(payload: Any?): AppointmentRequestFragment {
            val fragment = AppointmentRequestFragment()
            val bundle = Bundle()
            if (payload is Parcelable) bundle.putParcelable("KEY", payload)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            doctorInfo = it.getParcelable("KEY")
        }
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel = ViewModelProvider(this).get(AppointmentRequestViewModel::class.java)
        viewModel.headerMap = headerMap

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_appointment_request, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_appointment_request)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        ibtn_close?.setOnClickListener {
            if(snackBarSuccess!=null && snackBarSuccess!!.isShown) snackBarSuccess?.dismiss()
            mFragmentListener?.popTopMostFragment()
        }

        btn_send_request?.setOnClickListener {
            val fragment: AppointmentRequestDialogFragment = AppointmentRequestDialogFragment.newInstance()
            fragment.setOnDialogListener(object : AppointmentRequestDialogFragment.DialogListener {
                override fun onClickYes() {
                    fragment.dismiss()
                    if(doctorInfo?.id !=null && doctorInfo?.timeSlotInfo?.timeSlotId !=null){
                        viewModel.attemptAppointmentRequest(doctorInfo!!.id , doctorInfo!!.timeSlotInfo!!.timeSlotId)
                    }
                }
                override fun onClickNo() {
                    fragment.dismiss()
                }
            })
            fragment.show(childFragmentManager, "TAG")
        }

        card_view_top?.setOnClickListener {}
        card_view_bottom?.setOnClickListener {}

        initView()
        initObserver()

    }

    @ExperimentalStdlibApi
    private fun initView() {
        doctorInfo?.let {
            if (!it.profilePic.isNullOrEmpty()) {
                Glide.with(this)
                    .load(WebUrl.BASE_FILE + it.profilePic)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.colorLightestGrey)
                    .into(iv_profile)
            } else {
                Glide.with(this)
                    .load(R.drawable.img_avatar)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv_profile)
            }

            val fullName = it.name.split(" ").toMutableList()
            var output = ""
            for (name in fullName) {
                output += name.capitalize(Locale.getDefault()) + " "
            }
            tv_doc_name?.text = output
            if (it.doctorSpecialities.isNotEmpty()) tv_speciality?.text =
                it.doctorSpecialities[0].name


            it.date?.let { input ->
                val outFormat = DateTimeFormatter.ofPattern("MMMM")
                val inFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val nameOfMonth = outFormat.format(inFormat.parse(input))
                val dayOfMonth = input.split("-")[0]
                tv_date.text= dayOfMonth.plus(" ").plus(nameOfMonth)
            }

            it.timeSlotInfo?.slotFrom?.let {
                val inFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()) // 01-09-2020 ---> 1-9-2020
                val outFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()) // 01-09-2020 ---> 1-9-2020
                val dateTime = LocalDateTime.parse(it, inFormatter)
                val formattedDate: String =dateTime.format(outFormatter)
                tv_time.text=formattedDate
            }

            it.appointmentFees?.let {
                val fee = getString(R.string.title_dollar)
                    .plus(" ")
                    .plus(it)
                    .plus(" ")
                    .plus("/-")
                tv_appointment_fee.text=fee
            }
        }

    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) progress_bar?.visibility=View.VISIBLE
            else progress_bar?.visibility=View.INVISIBLE
        })

        viewModel.isViewEnable.observe(viewLifecycleOwner, Observer {
            if (it) btn_send_request?.visibility=View.VISIBLE
            else btn_send_request?.visibility=View.INVISIBLE
        })

        viewModel.resultAppointmentReq.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.isViewEnable.value=false
                    showSnackBar(it.data?.message.toString())
                }
                Status.FAILURE -> {
                    viewModel.isViewEnable.value=true
                    super.showSnackBar(it.errorMsg.toString(),R.color.colorRed)
                }
            }
        })

    }

       private fun showSnackBar(msg:String){
           snackBarSuccess = Snackbar.make(getRootView(), msg, Snackbar.LENGTH_INDEFINITE)
           snackBarSuccess?.view?.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.colorGreen))
           snackBarSuccess?.setActionTextColor(Color.WHITE)
           snackBarSuccess?.setAction(getString(R.string.action_ok)) {
            activity?.finish()
        }
            ?.show()
    }

    override fun onDestroyView() {
        snackBarSuccess?.dismiss()
        super.onDestroyView()

    }
}