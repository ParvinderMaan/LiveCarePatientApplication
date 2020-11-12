package com.app.patlivecare.prescription.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.R
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import kotlinx.android.synthetic.main.fragment_prescription_detail.*


class PrescriptionDetailFragment : Fragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private var medicalTestAdapter: MedicalTestAdapter?=null
    private var medicineAdapter: MedicineAdapter?=null
    companion object {
        fun newInstance() = PrescriptionDetailFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         medicalTestAdapter=MedicalTestAdapter()
         medicineAdapter=MedicineAdapter()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_prescription_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_prescription_detail)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)


        tv_medicines.isSelected=true
        tv_tests.isSelected=false
        tv_medicines?.setOnClickListener {
            tv_medicines.isSelected=true
            tv_tests.isSelected=false

            rv_detail?.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = medicineAdapter
            }
        }
        tv_tests?.setOnClickListener {
            tv_medicines.isSelected=false
            tv_tests.isSelected=true

            rv_detail?.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = medicalTestAdapter
            }
        }

        rv_detail?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = medicineAdapter
        }


        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }
    }

}