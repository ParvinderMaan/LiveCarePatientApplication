package com.app.patlivecare.doctor.view

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.extra.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.viewmodel.FindDoctorViewModel
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_find_doctor.*
import kotlinx.android.synthetic.main.fragment_find_doctor.group_empty_view
import kotlinx.android.synthetic.main.fragment_find_doctor.ibtn_close
import kotlinx.android.synthetic.main.fragment_find_doctor.progress_bar
import kotlinx.android.synthetic.main.fragment_find_doctor.toolbar_main
import kotlinx.android.synthetic.main.fragment_find_doctor.tv_header_title
import kotlinx.coroutines.*
import java.util.HashMap

class FindDoctorFragment : Fragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    var findDoctorAdapter:FindDoctorAdapter?=null
    private lateinit var viewModel: FindDoctorViewModel
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance(payload: Any?):FindDoctorFragment{
            val fragment = FindDoctorFragment()
            val bundle = Bundle()
            if (payload is String) bundle.putString("KEY",payload)
            fragment.arguments = bundle
            return fragment
        }
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
        findDoctorAdapter=FindDoctorAdapter()
        viewModel = ViewModelProvider(this).get(FindDoctorViewModel::class.java)
        viewModel.headers= headerMap

        arguments?.let {
            viewModel.specialityId = it.getString("KEY","")
        }
       // Log.e("FindDoctorFragment", "  "+ viewModel.specialityId)
        viewModel.fetchDoctors()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_find_doctor, container, false)
    }

    @SuppressLint("CheckResult")
    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_find_your_doctors)       // CATEGORY NAME =
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)

        initRecyclerView()

        ibtn_close?.setOnClickListener {
            activity?.finish()
        }
        tv_search?.setOnClickListener {
         // Toast.makeText(activity,"center...",Toast.LENGTH_SHORT).show()
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.SEARCH_DOCTOR_FRAGMENT)
            intent.putExtra("key_","")
            startActivity(intent)
        }


        initObserver()
    }

    private fun initRecyclerView() {
        rv_find_doctor?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = findDoctorAdapter
        }
        findDoctorAdapter?.setOnItemClickListener(object :
            FindDoctorAdapter.OnItemClickListener {
            override fun onItemClick(model: DoctorInfo, adapterPosition: Int) {
                mFragmentListener?.showFragment(FragmentType.DOCTOR_DETAIL_FRAGMENT,model)
            }

            override fun onItemBookClick(model: DoctorInfo, adapterPosition: Int) {
                mFragmentListener?.showFragment(FragmentType.APPOINTMENT_BOOKING_FRAGMENT,model)
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
                if (it){
                    group_empty_view?.visibility = View.VISIBLE
                    tv_search?.visibility = View.GONE
                }
            })

        viewModel.userPagedList.observe(viewLifecycleOwner, Observer {
            findDoctorAdapter?.submitList(it)
        })

    }



}