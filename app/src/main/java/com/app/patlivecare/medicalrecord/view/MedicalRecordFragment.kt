package com.app.patlivecare.medicalrecord.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.patlivecare.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import kotlinx.android.synthetic.main.fragment_medical_record.*

class MedicalRecordFragment : Fragment() {

    companion object {
        fun newInstance() =
            MedicalRecordFragment()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_medical_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_one?.setOnClickListener {
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.PAST_MEDICAL_HISTORY_FRAGMENT)
            startActivity(intent)
        }

        view_two?.setOnClickListener {
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.SURGICAL_HISTORY_FRAGMENT)
            startActivity(intent)
        }

        view_three?.setOnClickListener {
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.FAMILY_HISTORY_FRAGMENT)
            startActivity(intent)
        }

        view_four?.setOnClickListener {
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.ALLERGY_HISTORY_FRAGMENT)
            startActivity(intent)
        }

        view_five?.setOnClickListener {
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.ATTACHMENT_AND_REPORT_FRAGMENT)
            startActivity(intent)
        }

    }





}