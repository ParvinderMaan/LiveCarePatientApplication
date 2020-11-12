package com.app.patlivecare.doctor.view

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.viewmodel.DoctorDetailViewModel
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_doctor_detail.*
import java.util.*


class DoctorDetailFragment : BaseFragment() {
    private var doctorInfo: DoctorInfo? = null
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private var languageAdapter: LanguageAdapter? = null
    private lateinit var viewModel: DoctorDetailViewModel
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance(payload: Any?): DoctorDetailFragment {
            val fragment = DoctorDetailFragment()
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
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageAdapter = LanguageAdapter()
        viewModel = ViewModelProvider(this).get(DoctorDetailViewModel::class.java)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel.headerMap = headerMap
        arguments?.let {
            doctorInfo = it.getParcelable("KEY")
        }
        doctorInfo?.id?.let { it1 -> viewModel.fetchDoctorDetails(it1) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_doctor_detail, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initObserver()

        doctorInfo?.name?.let {
            tv_doc_name?.text = getString(R.string.title_dr).plus(" ").plus(it)
        }
        doctorInfo?.doctorSpecialities?.get(0)?.name.let { tv_speciality.text = it }

        if(!doctorInfo?.profilePic.isNullOrEmpty()){
            Glide.with(this)
                .load(WebUrl.BASE_FILE + doctorInfo?.profilePic)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.img_avatar)
                .into(civ_profile)
        }else{
            Glide.with(this)
                .load(R.drawable.img_avatar)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(civ_profile)
        }

        doctorInfo?.rating?.let {
            showRatingBar(it) }


    }

    private fun showRatingBar(ratingCount: Int) {
        when (ratingCount) {
            1 -> {
                iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_two.setImageResource(R.drawable.ic_star_off_yellow)
                iv_star_three.setImageResource(R.drawable.ic_star_off_yellow)
                iv_star_four.setImageResource(R.drawable.ic_star_off_yellow)
                iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
            }
            2 -> {
                iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_three.setImageResource(R.drawable.ic_star_off_yellow)
                iv_star_four.setImageResource(R.drawable.ic_star_off_yellow)
                iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
            }
            3 -> {
                iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_three.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_four.setImageResource(R.drawable.ic_star_off_yellow)
                iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
            }
            4 -> {
                iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_three.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_four.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_five.setImageResource(R.drawable.ic_star_off_yellow)
            }
            5 -> {
                iv_star_one.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_two.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_three.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_four.setImageResource(R.drawable.ic_star_on_yellow)
                iv_star_five.setImageResource(R.drawable.ic_star_on_yellow)
            }
        }
    }

    private fun initListener() {
        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }
        fbtn_book_appointment?.setOnClickListener {
            mFragmentListener?.showFragment(FragmentType.APPOINTMENT_BOOKING_FRAGMENT,doctorInfo)
        }
        tv_more?.setOnClickListener {
            when (tv_more.text) {
               getString(R.string.action_more) -> {
                    tv_about_me.maxLines = Integer.MAX_VALUE
                    tv_more?.text = getString(R.string.action_less)
                }
                getString(R.string.action_less) -> {
                    tv_about_me.maxLines = 5
                    tv_more?.text = getString(R.string.action_more)
                }
            }

        }

        cv_education?.setOnClickListener {}
        cv_appointment_fee?.setOnClickListener {}
        cv_language?.setOnClickListener {}
    }

    @ExperimentalStdlibApi
    private fun populateUi(info: DoctorDetailResponse.Data?) {

        if(info?.about.isNullOrEmpty()){
            tv_about_me.text = "No Information"
            tv_more.visibility = View.INVISIBLE
        }else{
            tv_about_me.text = info?.about?.capitalize(Locale.ROOT)
        }


        // tv_is_verified.text=""
        info?.doctorEducation?.let {
            if (it.isNotEmpty()) {
                it.forEach { info->
                    tv_education?.append(info.degreeName.plus(" | ").plus(info.instituteName))
                    tv_education?.append("\n")
                }
            } else {
                tv_education?.text = "No Information"
            }
        }


        info?.doctorLangauges?.let {
            if (it.isNotEmpty()) {
                rv_language?.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    adapter = languageAdapter
                    languageAdapter?.submitList(it)
                }
            } else {
                rv_language?.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    adapter = languageAdapter
                    val list = mutableListOf<DoctorDetailResponse.Language>()
                    list.add(DoctorDetailResponse.Language(0, "No Information"))
                    languageAdapter?.submitList(list)
                }
            }
        }

        info?.appointmentFees?.let {
            val fee = "$"
                .plus(" ")
                .plus(it)
                .plus(" ")
                .plus("/-")
            tv_appointment_fee?.text = fee
        }

        doctorInfo?.rating?.let {
            showRatingBar(it)
        }

        info?.reviewsCount?.let {
            tv_review_count?.text = it.toString()
            tv_review_count?.append(" ")
            tv_review_count?.append(getString(R.string.title_review))
        }
        info?.reviewsCount?.let {
            if(it==0) tv_review_count?.setOnClickListener(null)
            else tv_review_count?.setOnClickListener {
                mFragmentListener?.showFragment(FragmentType.DOCTOR_REVIEW_FRAGMENT,doctorInfo)
            }
        }



    }

    @ExperimentalStdlibApi
    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    progress_bar?.show()
                    sv_main?.visibility = View.INVISIBLE
                } else {
                    progress_bar?.hide()
                    sv_main?.visibility = View.VISIBLE
                }
            })


        viewModel.resultDoctorDetail.observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.data.let { itt -> populateUi(itt) }
                }
                Status.FAILURE -> {
                  showSnackBar(it.errorMsg.toString())
                }
            }
        })

        viewModel.lstOfLanguage.observe(viewLifecycleOwner,Observer{


        })


    }

}