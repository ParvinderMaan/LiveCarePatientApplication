package com.app.patlivecare.prescription.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.prescription.model.PrescriptionInfo
import kotlinx.android.synthetic.main.fragment_prescription.*


class PrescriptionFragment : Fragment() {
  var prescriptionAdapter: PrescriptionAdapter?=null

    companion object {
        fun newInstance() =
            PrescriptionFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prescriptionAdapter=
            PrescriptionAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_prescription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_prescription?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = prescriptionAdapter
            prescriptionAdapter?.setOnItemClickListener(object :
                PrescriptionAdapter.OnItemClickListener {
                override fun onItemClick(model: PrescriptionInfo, adapterPosition: Int) {
                    val intent= Intent(activity, MinorActivity::class.java)
                    intent.putExtra("fragment_type", FragmentType.PRESCRIPTION_DETAIL_FRAGMENT)
                    startActivity(intent)
                }

            })
            prescriptionAdapter?.addAll(getDummyDData())
        }
    }
    private fun getDummyDData(): List<PrescriptionInfo> {
        val lstOfTopDoctor: ArrayList<PrescriptionInfo> = ArrayList()
        lstOfTopDoctor.add(
            PrescriptionInfo(
                1,
                "Required Medicines",
                "Abd efsdd ",
                12345
            )
        )
        lstOfTopDoctor.add(
            PrescriptionInfo(
                2,
                "Abcdef Gh",
                "Ab cdef Gh",
                12345
            )
        )
        return lstOfTopDoctor
    }



}