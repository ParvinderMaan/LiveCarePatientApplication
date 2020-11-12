package com.app.patlivecare.home.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.model.SpecialityInfo
import com.app.patlivecare.doctor.view.DoctorSpecialityAdapter
import com.app.patlivecare.doctor.view.TopDoctorAdapter
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.home.viewmodel.HomeViewModel
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import kotlin.collections.set


class HomeFragment : BaseFragment() {
    private var mLayoutAnimation: LayoutAnimationController?=null
    private var topDoctorAdapter: TopDoctorAdapter? = null
    private var specialityAdapter: DoctorSpecialityAdapter? = null
    private var sharedPrefs: SharedPrefHelper? = null
    private lateinit var viewModel: HomeViewModel
    private var job:Job?=null
    private var isAnimateOnce:Boolean=false
    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun getRootView(): View {
       return sv_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topDoctorAdapter = TopDoctorAdapter()
        specialityAdapter = DoctorSpecialityAdapter(R.layout.list_item_speciality_i)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel.headerMap = headerMap

     //   viewModel.fetchSpeciality()  now !!!
        viewModel.fetchDashboardInfo()
        mLayoutAnimation =AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_slide_from_right)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        progress_bar?.progress = 0
        tv_find_with_speciality.visibility=View.INVISIBLE
        rv_speciality.visibility=View.INVISIBLE
        tv_our_top_doctors.visibility=View.INVISIBLE
        rv_our_top_doctors.visibility=View.INVISIBLE
        tv_view_all_speciality.visibility=View.INVISIBLE
        tv_more_top_doctor.visibility=View.INVISIBLE
        cv_profile_complete.visibility=View.INVISIBLE

        sharedPrefs?.read(SharedPrefHelper.KEY_PROFILE_PERCENT, 100)?.let {
            if (it == 100) cv_profile_complete?.visibility = View.GONE
            else   viewModel.fetchPatientInfo()  // now!!!
        }

        rv_speciality?.apply {
            layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = specialityAdapter
            if(!isAnimateOnce)layoutAnimation=mLayoutAnimation
            specialityAdapter?.setOnItemClickListener(object :
                DoctorSpecialityAdapter.OnItemClickListener {
                override fun onItemClick(model: SpecialityInfo, adapterPosition: Int) {
                    val intent = Intent(activity, MinorActivity::class.java)
                    intent.putExtra("fragment_type", FragmentType.FIND_DOCTOR_FRAGMENT)
                    intent.putExtra("key_", model.id.toString())
                    startActivity(intent)
                }

            })
        }
        rv_our_top_doctors?.apply {
            layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = topDoctorAdapter
            if(!isAnimateOnce)layoutAnimation=mLayoutAnimation
            topDoctorAdapter?.setOnItemClickListener(object :
                TopDoctorAdapter.OnItemClickListener {
                override fun onItemClick(model: DoctorInfo, adapterPosition: Int) {
                    val intent = Intent(activity, MinorActivity::class.java)
                    intent.putExtra("key_", model)
                    intent.putExtra("fragment_type", FragmentType.DOCTOR_DETAIL_FRAGMENT)
                    startActivity(intent)
                }

                override fun onItemBookNowClick(model: DoctorInfo, adapterPosition: Int) {
                    val intent = Intent(activity, MinorActivity::class.java)
                    intent.putExtra("fragment_type", FragmentType.APPOINTMENT_BOOKING_FRAGMENT)
                    intent.putExtra("key_", model)
                    startActivity(intent)
                }
            })
        }

        initObserver()

        isAnimateOnce=true
    }



    private fun initListener() {
        btn_book_now.setOnClickListener {
            val intent = Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.FIND_DOCTOR_FRAGMENT)
            startActivity(intent)
        }

        tv_view_all_speciality?.setOnClickListener {
            val intent = Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.DOCTOR_SPECIALITY_FRAGMENT)
            startActivity(intent)
        }

        tv_more_top_doctor?.setOnClickListener {
            val intent = Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.TOP_DOCTOR_FRAGMENT)
            startActivity(intent)
        }

        iv_category?.setOnClickListener {}

        btn_show_profile?.setOnClickListener {
            if (activity is HomeActivity) (activity as HomeActivity).showProfileFragment()
            viewModel.resultPatientInfo.value=null
        }

    }




    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) pro_bar?.show()
                else pro_bar?.hide()
            })


        viewModel.lstOfSpeciality.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                specialityAdapter?.submitList(it.take(10))
                tv_find_with_speciality.visibility = View.VISIBLE
                rv_speciality.visibility = View.VISIBLE
                tv_view_all_speciality.visibility = View.VISIBLE

            }
        })

        viewModel.lstOfTopDoctor.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                topDoctorAdapter?.submitList(it)
                tv_our_top_doctors.visibility = View.VISIBLE
                rv_our_top_doctors.visibility = View.VISIBLE
                tv_more_top_doctor.visibility = View.VISIBLE
            }
        })



        viewModel.resultantSpeciality.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.lstOfSpeciality.value = it.data?.data?.listOfSpeciality

                }
                Status.FAILURE -> {

                }
            }
        })
        viewModel.resultPatientInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            when (it.status) {
                Status.SUCCESS -> {
                    val profilePercent = it.data?.data?.percentageProfileComplete
                    if (profilePercent == 100) cv_profile_complete?.visibility = View.GONE
                    else {
                        cv_profile_complete.visibility = View.VISIBLE
                        profilePercent?.let { progress ->
//                          progress_bar?.progress = progress
//                          tv_profile_percent?.text = progress.toString()
//                          tv_profile_percent.append("%")
                            tv_profile_percent_?.text =
                                getString(R.string.title_profile_completion_rate)
                            tv_profile_percent_?.append(" ")
                            tv_profile_percent_?.append(progress.toString())
                            tv_profile_percent_.append("%")

                            job = viewLifecycleOwner.lifecycleScope.launch {

                                flow {
                                    (1..progress).forEach { value ->
                                        delay(20)
                                        emit(value)
                                    }
                                }.flowOn(Dispatchers.IO)
                                    .collect { xXx ->
                                        withContext(Dispatchers.Main) {
                                            progress_bar?.progress = xXx
                                            tv_profile_percent?.text = xXx.toString()
                                            tv_profile_percent.append("%")
                                        }
                                    }
                            }

                            sharedPrefs?.builder()
                                ?.write(SharedPrefHelper.KEY_PROFILE_PERCENT, progress)
                                ?.build()
                        }
                    }
                }
                Status.FAILURE -> {
                    it.errorMsg?.let { it1 -> showSnackBar(it1) }
                }
            }
        })

        viewModel.resultDashboardInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.lstOfSpeciality.value = it.data?.listOfSpeciality
                    viewModel.lstOfTopDoctor.value = it.data?.listOfDoctor
                }
                Status.FAILURE -> {

                }
            }
        })

    }

    override fun onDestroyView() {
        job?.cancel()
        super.onDestroyView()
    }

}