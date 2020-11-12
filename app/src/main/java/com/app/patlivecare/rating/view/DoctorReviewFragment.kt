package com.app.patlivecare.rating.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.rating.viewmodel.DoctorReviewViewModel
import kotlinx.android.synthetic.main.fragment_doctor_review.*
import kotlinx.android.synthetic.main.fragment_doctor_review.cl_root
import kotlinx.android.synthetic.main.fragment_doctor_review.group_empty_view
import kotlinx.android.synthetic.main.fragment_doctor_review.progress_bar
import kotlinx.android.synthetic.main.fragment_doctor_review.toolbar_main
import kotlinx.android.synthetic.main.fragment_doctor_review.tv_header_title

import java.util.HashMap

class DoctorReviewFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private lateinit var viewModel: DoctorReviewViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private var doctorInfo: DoctorInfo? = null
    var reviewAdapter:DoctorReviewAdapter?=null

    companion object {
        fun newInstance(payload: Any?): DoctorReviewFragment {
            val fragment = DoctorReviewFragment()
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
        viewModel = ViewModelProvider(this).get(DoctorReviewViewModel::class.java)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel.headerMap = headerMap
        reviewAdapter=DoctorReviewAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_doctor_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_user_review)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)
        doctorInfo?.id?.let { viewModel.fetchDoctorReview(it) }
        initRecyclerView()
        initObserver()
        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.show()
                else progress_bar?.hide()
            })

        viewModel.resultDoctorReview.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.data?.let {lstOfReview ->
                        viewModel.lstOfReview.value = lstOfReview
                    }
                }
                Status.FAILURE -> {
                    showSnackBar(it.errorMsg.toString())
                }
            }
        })

        viewModel.lstOfReview.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                group_empty_view?.visibility = View.VISIBLE
            } else {
                reviewAdapter?.submitList(it)
            }

        })

    }
    private fun initRecyclerView() {
        rv_user_review?.apply {
            val llManager = LinearLayoutManager(activity)
            layoutManager=llManager
            adapter = reviewAdapter
        }
    }

}