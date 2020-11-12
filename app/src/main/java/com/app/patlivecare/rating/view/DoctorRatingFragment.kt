package com.app.patlivecare.rating.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.app.patlivecare.rating.viewmodel.DoctorRatingViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_doctor_rating.*
import kotlinx.android.synthetic.main.fragment_doctor_rating.cl_root
import kotlinx.android.synthetic.main.fragment_doctor_rating.ibtn_close
import kotlinx.android.synthetic.main.fragment_doctor_rating.iv_star_five
import kotlinx.android.synthetic.main.fragment_doctor_rating.iv_star_four
import kotlinx.android.synthetic.main.fragment_doctor_rating.iv_star_one
import kotlinx.android.synthetic.main.fragment_doctor_rating.iv_star_three
import kotlinx.android.synthetic.main.fragment_doctor_rating.iv_star_two
import kotlinx.android.synthetic.main.fragment_doctor_rating.progress_bar
import kotlinx.android.synthetic.main.fragment_doctor_rating.tv_doc_name
import kotlinx.android.synthetic.main.fragment_doctor_rating.tv_speciality
import java.util.HashMap

class DoctorRatingFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private var appointmentInfo: AppointmentInfo? = null
    private lateinit var viewModel: DoctorRatingViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private var snackBarSuccess:Snackbar?=null

    companion object {
        fun newInstance(payload: Any?): DoctorRatingFragment {
            val fragment = DoctorRatingFragment()
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
            appointmentInfo = it.getParcelable("KEY")
        }
        viewModel = ViewModelProvider(this).get(DoctorRatingViewModel::class.java)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel.headerMap = headerMap
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_doctor_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appointmentInfo?.appointmentId?.let { viewModel.fetchDoctorRating(it) }


        if( appointmentInfo?.firstName !=null && appointmentInfo?.lastName !=null){
            tv_doc_name?.text = getString(R.string.title_dr)
                                .plus(" ")
                                .plus(appointmentInfo?.firstName)
                                .plus(" ")
                                .plus(appointmentInfo?.lastName)
        }


        appointmentInfo?.doctorSpecialities?.get(0)?.name.let { tv_speciality.text = it }

        if(!appointmentInfo?.profilePic.isNullOrEmpty()){
            Glide.with(this)
                .load(WebUrl.BASE_FILE + appointmentInfo?.profilePic)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.img_avatar)
                .into(iv_doctor)
        }else{
            Glide.with(this)
                .load(R.drawable.img_avatar)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_doctor)
        }
        initListener()
        initObserver()

    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            // mFragmentListener?.popTopMostFragment()
            activity?.finish()

        }
        fbtn_submit?.setOnClickListener {
            val ratingCount = getSelectedRating()
//          Log.e("ratingCount: ",ratingCount.toString())
            appointmentInfo?.appointmentId?.let { id -> viewModel.addDoctorRatingAndReview(id,ratingCount) }
        }

        iv_star_one?.setOnClickListener {
            viewModel.isOneStarSel.value =  viewModel.isOneStarSel.value==false
        }
        iv_star_two?.setOnClickListener {
            viewModel.isTwoStarSel.value = viewModel.isTwoStarSel.value==false
        }
        iv_star_three?.setOnClickListener {
            viewModel.isThreeStarSel.value = viewModel.isThreeStarSel.value==false
        }
        iv_star_four?.setOnClickListener {
            viewModel.isFourStarSel.value = viewModel.isFourStarSel.value==false
        }
        iv_star_five?.setOnClickListener {
            viewModel.isFiveStarSel.value = viewModel.isFiveStarSel.value==false
        }

    }

    private fun getSelectedRating(): Int {
        return when {
            viewModel.isFiveStarSel.value==true -> {
                5
            }
            viewModel.isFourStarSel.value==true -> {
                4
            }
            viewModel.isThreeStarSel.value==true -> {
                3
            }
            viewModel.isTwoStarSel.value==true -> {
                2
            }
            viewModel.isOneStarSel.value==true -> {
                1
            }
            else -> 0
        }
    }

    private fun showRatingBar(rating: Int) {
        when (rating) {
            1 -> {
//                iv_star_one.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_two.setImageResource(R.drawable.ic_star_off_turq_)
//                iv_star_three.setImageResource(R.drawable.ic_star_off_turq_)
//                iv_star_four.setImageResource(R.drawable.ic_star_off_turq_)
//                iv_star_five.setImageResource(R.drawable.ic_star_off_turq_)
                viewModel.isOneStarSel.value=true
            }
            2 -> {
//                iv_star_one.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_two.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_three.setImageResource(R.drawable.ic_star_off_turq_)
//                iv_star_four.setImageResource(R.drawable.ic_star_off_turq_)
//                iv_star_five.setImageResource(R.drawable.ic_star_off_turq_)
                viewModel.isTwoStarSel.value=true
            }
            3 -> {
//                iv_star_one.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_two.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_three.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_four.setImageResource(R.drawable.ic_star_off_turq_)
//                iv_star_five.setImageResource(R.drawable.ic_star_off_turq_)
                viewModel.isThreeStarSel.value=true
            }
            4 -> {
//                iv_star_one.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_two.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_three.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_four.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_five.setImageResource(R.drawable.ic_star_off_turq_)
                viewModel.isFourStarSel.value=true
            }
            5 -> {
//                iv_star_one.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_two.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_three.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_four.setImageResource(R.drawable.ic_star_on_turq_)
//                iv_star_five.setImageResource(R.drawable.ic_star_on_turq_)
                viewModel.isFiveStarSel.value=true
            }
        }
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    progress_bar?.show()
                    mcv_bottom?.visibility = View.INVISIBLE
                } else {
                    progress_bar?.hide()
                    mcv_bottom?.visibility = View.VISIBLE
                }
            })
        viewModel.isViewEnable.observe(viewLifecycleOwner,
            Observer {
                if (it) fbtn_submit?.visibility=View.VISIBLE
                 else fbtn_submit?.visibility=View.INVISIBLE
            })

        viewModel.resultFetchDoctorRating.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.data?.userRating?.let { rating  -> showRatingBar(rating) }
                }
                Status.FAILURE -> {
                    showSnackBar(it.errorMsg.toString())
                }
            }
        })

        viewModel.resultAddDoctorRating.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    showSnackBar(it.data?.message.toString())
                }
                Status.FAILURE -> {
                    super.showSnackBar(it.errorMsg.toString(),R.color.colorRed)
                }
            }
        })

        viewModel.isOneStarSel.observe(viewLifecycleOwner, Observer {
            if(it) {
                iv_star_one?.setImageResource(R.drawable.ic_star_on_turq_)
                fbtn_submit?.show()
            }
            else {
                iv_star_one?.setImageResource(R.drawable.ic_star_off_turq_)
                viewModel.isTwoStarSel.value=false
                viewModel.isThreeStarSel.value=false
                viewModel.isFourStarSel.value=false
                viewModel.isFiveStarSel.value=false
                fbtn_submit?.hide()
            }
        })

        viewModel.isTwoStarSel.observe(viewLifecycleOwner, Observer {
            if(it)  viewModel.isOneStarSel.value=true
            if(it)   iv_star_two?.setImageResource(R.drawable.ic_star_on_turq_)
            else iv_star_two?.setImageResource(R.drawable.ic_star_off_turq_)
        })

        viewModel.isThreeStarSel.observe(viewLifecycleOwner, Observer {
            if(it)  viewModel.isOneStarSel.value=true
            if(it)  viewModel.isTwoStarSel.value=true
            if(it)   iv_star_three?.setImageResource(R.drawable.ic_star_on_turq_)
            else iv_star_three?.setImageResource(R.drawable.ic_star_off_turq_)
        })

        viewModel.isFourStarSel.observe(viewLifecycleOwner, Observer {
            if(it)  viewModel.isOneStarSel.value=true
            if(it)  viewModel.isTwoStarSel.value=true
            if(it)  viewModel.isThreeStarSel.value=true
            if(it)   iv_star_four?.setImageResource(R.drawable.ic_star_on_turq_)
            else iv_star_four?.setImageResource(R.drawable.ic_star_off_turq_)
        })

        viewModel.isFiveStarSel.observe(viewLifecycleOwner, Observer {
            if(it)  viewModel.isOneStarSel.value=true
            if(it)  viewModel.isTwoStarSel.value=true
            if(it)  viewModel.isThreeStarSel.value=true
            if(it)  viewModel.isFourStarSel.value=true
            if(it)  iv_star_five?.setImageResource(R.drawable.ic_star_on_turq_)
            else    iv_star_five?.setImageResource(R.drawable.ic_star_off_turq_)
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
}