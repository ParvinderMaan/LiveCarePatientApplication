package com.app.patlivecare.doctor.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.extra.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.viewmodel.TopDoctorViewModel
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_top_doctor.*
import kotlinx.android.synthetic.main.fragment_top_doctor.group_empty_view
import kotlinx.android.synthetic.main.fragment_top_doctor.ibtn_close
import kotlinx.android.synthetic.main.fragment_top_doctor.progress_bar
import kotlinx.android.synthetic.main.fragment_top_doctor.toolbar_main
import kotlinx.android.synthetic.main.fragment_top_doctor.tv_header_title
import java.util.HashMap


class TopDoctorFragment : Fragment() {
    private  var topDoctorAdapter: TopDoctorsAdapter?=null
    private lateinit var viewModel: TopDoctorViewModel
    private var sharedPrefs: SharedPrefHelper? = null
    private var mFragmentListener: HomeFragmentSelectedListener? = null

    companion object {
        fun newInstance() = TopDoctorFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(TopDoctorViewModel::class.java)
        viewModel.headers= headerMap
        topDoctorAdapter = TopDoctorsAdapter()
        viewModel.fetchTopDoctors()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_top_doctor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_our_top_doctors)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        ibtn_close.setOnClickListener {
            activity?.finish()
        }
        initRecyclerView()



        initObserver()


    }

    private fun initRecyclerView() {

        rv_top_doctors?.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = topDoctorAdapter
        }

        topDoctorAdapter?.setOnItemClickListener(object :
            TopDoctorsAdapter.OnItemClickListener {
            override fun onItemClick(model: DoctorInfo, adapterPosition: Int) {
                mFragmentListener?.showFragment(FragmentType.DOCTOR_DETAIL_FRAGMENT,model)

            }

            override fun onItemBookNowClick(model: DoctorInfo, adapterPosition: Int) {
                val intent= Intent(activity, MinorActivity::class.java)
                intent.putExtra("fragment_type", FragmentType.APPOINTMENT_BOOKING_FRAGMENT)
                intent.putExtra("key_",model)
                startActivity(intent)
            }
        })

    }
    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner,
            Observer {
                if (it) progress_bar?.show()
                else progress_bar?.hide()
            })

        viewModel.isListEmpty.observe(viewLifecycleOwner,
            Observer {
                if (it) group_empty_view?.visibility = View.VISIBLE })

        viewModel.userPagedList.observe(viewLifecycleOwner, Observer {
            topDoctorAdapter?.submitList(it)
        })

//        viewModel.resultantTopDoctor.observe(viewLifecycleOwner, Observer {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    topDoctorAdapter?.submitList(it.data?.data?.dataList)
//                }
//                Status.FAILURE -> Toast.makeText(activity,it.errorMsg.toString(),Toast.LENGTH_SHORT).show()
//            }
//
//
//            })
  }
}