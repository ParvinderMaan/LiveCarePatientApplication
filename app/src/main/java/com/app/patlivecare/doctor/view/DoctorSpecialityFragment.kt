package com.app.patlivecare.doctor.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.doctor.model.SpecialityInfo
import com.app.patlivecare.doctor.viewmodel.DoctorSpecialityViewModel
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_doctor_speciality.*
import java.util.*


class DoctorSpecialityFragment : BaseFragment() {
    var docSpecialityAdapter:DoctorSpecialityAdapter?=null
    private var sharedPrefs: SharedPrefHelper? = null
    private lateinit var viewModel: DoctorSpecialityViewModel

    companion object {
        fun newInstance() = DoctorSpecialityFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        docSpecialityAdapter= DoctorSpecialityAdapter(R.layout.list_item_speciality_ii)
        viewModel = ViewModelProvider(this).get(DoctorSpecialityViewModel::class.java)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel.fetchSpeciality()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doctor_speciality, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_find_with_specialities)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(
            toolbar_main
        )

        initRecyclerView()
        initObserver()
        ibtn_close?.setOnClickListener {
            activity?.finish()
        }
    }

    private fun initRecyclerView() {
        rv_doctor_speciality?.apply {
            val llManager = GridLayoutManager(activity, 2)
            layoutManager=llManager
            adapter = docSpecialityAdapter
        }
        docSpecialityAdapter?.setOnItemClickListener(object :
            DoctorSpecialityAdapter.OnItemClickListener {
            override fun onItemClick(model: SpecialityInfo, adapterPos: Int) {
                val intent = Intent(activity, MinorActivity::class.java)
                intent.putExtra("fragment_type", FragmentType.FIND_DOCTOR_FRAGMENT)
                intent.putExtra("key_", model.id.toString())
                startActivity(intent)

            }
        })


    }





    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) progress_bar?.show()
            else progress_bar?.hide()
        })

        viewModel.resultantSpeciality.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> viewModel.lstOfSpeciality.value = it.data?.data?.listOfSpeciality
                Status.FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.lstOfSpeciality.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                group_empty_view.visibility = View.GONE
                docSpecialityAdapter?.submitList(it)
                rv_doctor_speciality?.scheduleLayoutAnimation()
            } else {
                group_empty_view.visibility = View.VISIBLE
                docSpecialityAdapter?.submitList(null)
            }
        })


    }

    override fun getRootView(): View {
        return cl_root
    }
}