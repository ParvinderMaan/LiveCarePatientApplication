package com.app.patlivecare.extra

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.appointment.view.AppointmentBookingFragment
import com.app.patlivecare.appointment.view.AppointmentRequestFragment
import com.app.patlivecare.appointment.view.TimeSlotFragment
import com.app.patlivecare.blog.model.BlogInfo
import com.app.patlivecare.blog.view.BlogDetailFragment
import com.app.patlivecare.chat.ChatFragment
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.doctor.view.*
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.medicalrecord.view.*
import com.app.patlivecare.miscellaneous.view.ContactUsFragment
import com.app.patlivecare.miscellaneous.view.PrivacyPolicyFragment
import com.app.patlivecare.miscellaneous.view.TermAndConditionFragment
import com.app.patlivecare.password.view.ChangePasswordFragment
import com.app.patlivecare.password.view.ForgotPasswordFragment
import com.app.patlivecare.password.view.VerificationFragment
import com.app.patlivecare.prescription.view.PrescriptionDetailFragment
import com.app.patlivecare.rating.view.AddDoctorReviewFragment
import com.app.patlivecare.rating.view.DoctorRatingFragment
import com.app.patlivecare.rating.view.DoctorReviewFragment
import com.app.patlivecare.rating.view.ReportFragment
import com.app.patlivecare.videocall.view.WaitingRoomFragment


class MinorActivity : AppCompatActivity(), HomeFragmentSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_minor)
        val fragmentType = intent.getStringExtra("fragment_type")

        showFragment(fragmentType)

    }

    override fun showFragment(fragmentName: String, payload: Any?) {
        when (fragmentName) {
            FragmentType.TERM_AND_CONDITION_FRAGMENT -> {

                supportFragmentManager.beginTransaction()
                    .apply {
                        add(R.id.fl_container, TermAndConditionFragment.newInstance())
                        commit()
                    }
            }
            FragmentType.PRIVACY_POLICY_FRAGMENT -> {

                supportFragmentManager.beginTransaction()
                    .apply {
                        add(R.id.fl_container, PrivacyPolicyFragment.newInstance())
                        commit()
                    }
            }
            FragmentType.FORGOT_PASSWORD_FRAGMENT-> {

                supportFragmentManager.beginTransaction()
                    .apply {
                        add(R.id.fl_container, ForgotPasswordFragment.newInstance())
                        commit()
                    }
            }
            FragmentType.VERIFICATION_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        add(R.id.fl_container, VerificationFragment.newInstance(intent))
                        commit()
                    }
            }
            FragmentType.BLOG_DETAIL_FRAGMENT-> {
                val xXx = intent.getParcelableExtra<BlogInfo>("key_")
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, BlogDetailFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }
            FragmentType.TOP_DOCTOR_FRAGMENT-> {

                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, TopDoctorFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
            }
            FragmentType.DOCTOR_SPECIALITY_FRAGMENT-> {

                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, DoctorSpecialityFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.FIND_DOCTOR_FRAGMENT-> {
                val xXx = intent.getStringExtra("key_")
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, FindDoctorFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.PAST_MEDICAL_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, PastMedicalHistoryFragment.newInstance(),FragmentType.PAST_MEDICAL_HISTORY_FRAGMENT)
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.SURGICAL_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, SurgicalHistoryFragment.newInstance(),FragmentType.SURGICAL_HISTORY_FRAGMENT)
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.FAMILY_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, FamilyHistoryFragment.newInstance(),FragmentType.FAMILY_HISTORY_FRAGMENT)
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.ALLERGY_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AllergyHistoryFragment.newInstance(),FragmentType.ALLERGY_HISTORY_FRAGMENT)
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.ATTACHMENT_AND_REPORT_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AttachmentAndReportFragment.newInstance(),FragmentType.ATTACHMENT_AND_REPORT_FRAGMENT)
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.ADD_PAST_MEDICAL_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AddPastMedicalHistoryFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
            }
            FragmentType.ADD_SURGICAL_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AddSurgicalHistoryFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
            }
            FragmentType.ADD_FAMILY_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AddFamilyHistoryFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.ADD_ALLERGY_HISTORY_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AddAllergyHistoryFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.ADD_ATTACHMENT_AND_REPORT_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AddAttachmentAndReportFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.APPOINTMENT_BOOKING_FRAGMENT-> {
                var xXx:DoctorInfo?=null
                if(intent.getParcelableExtra<DoctorInfo>("key_")!=null)
                    xXx = intent.getParcelableExtra("key_")
                if(payload!=null && payload is DoctorInfo)
                    xXx = payload
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AppointmentBookingFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.DOCTOR_DETAIL_FRAGMENT-> {
                var xXx:DoctorInfo?=null
                if(intent.getParcelableExtra<DoctorInfo>("key_")!=null)
                 xXx = intent.getParcelableExtra("key_")
                if(payload!=null && payload is DoctorInfo)
                 xXx = payload
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, DoctorDetailFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.TIME_SLOT_FRAGMENT ->{
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, TimeSlotFragment.newInstance(payload))
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.APPOINTMENT_REQUEST_FRAGMENT ->{
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AppointmentRequestFragment.newInstance(payload))
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.WAITING_ROOM_FRAGMENT ->{
                var xXx:AppointmentInfo?=null
                if(intent.getParcelableExtra<AppointmentInfo>("key_")!=null)
                    xXx = intent.getParcelableExtra("key_")
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, WaitingRoomFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }
            FragmentType.DOCTOR_RATING_FRAGMENT ->{
                var xXx:AppointmentInfo?=null
                if(intent.getParcelableExtra<AppointmentInfo>("key_")!=null)
                    xXx = intent.getParcelableExtra("key_")
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, DoctorRatingFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }
            FragmentType.ADD_DOCTOR_REVIEW_FRAGMENT ->{
                val xXx: AppointmentInfo?
                xXx= intent.getParcelableExtra("key_")
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, AddDoctorReviewFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.DOCTOR_REVIEW_FRAGMENT ->{
                val xXx = intent.getParcelableExtra<DoctorInfo>("key_")
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, DoctorReviewFragment.newInstance(xXx))
                        addToBackStack(null)
                        commit()
                    }
            }

            FragmentType.CONTACT_US_FRAGMENT -> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, ContactUsFragment.newInstance())
                        addToBackStack(FragmentType.CONTACT_US_FRAGMENT)
                        commit()
                    }
            }
            FragmentType.CHANGE_PASSWORD_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, ChangePasswordFragment.newInstance())
                        addToBackStack(FragmentType.CONTACT_US_FRAGMENT)
                        commit()
                    }
            }

            FragmentType.REPORT_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, ReportFragment.newInstance())
                        addToBackStack(FragmentType.REPORT_FRAGMENT)
                        commit()
                    }
            }
            FragmentType.CHAT_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, ChatFragment.newInstance())
                        addToBackStack(FragmentType.CHAT_FRAGMENT)
                        commit()
                    }
            }
            FragmentType.PRESCRIPTION_DETAIL_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, PrescriptionDetailFragment.newInstance())
                        addToBackStack(FragmentType.PRESCRIPTION_DETAIL_FRAGMENT)
                        commit()
                    }
            }
            FragmentType.REPORT_VIEWER_FRAGMENT-> {
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, ReportViewerFragment.newInstance(payload))
                        addToBackStack(FragmentType.REPORT_VIEWER_FRAGMENT)
                        commit()
                    }
            }

            FragmentType.SEARCH_DOCTOR_FRAGMENT-> {
                val xXx = intent.getStringExtra("key_")
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container, SearchDoctorFragment.newInstance(payload))
                        addToBackStack(FragmentType.SEARCH_DOCTOR_FRAGMENT)
                        commit()
                    }
            }
            FragmentType.MEDICAL_RECORD_FRAGMENT ->{
                supportFragmentManager.beginTransaction()
                    .apply {
                        replace(R.id.fl_container,   MedicalRecordFragment.newInstance())
                        addToBackStack(FragmentType.SEARCH_DOCTOR_FRAGMENT)
                        commit()
                    }
            }
        }
    }

    override fun popTillFragment(tag: String, flag: Int) {

    }

    override fun popTopMostFragment() {
        if(supportFragmentManager.backStackEntryCount>1){
            supportFragmentManager.popBackStack()
        }else{
           finish()
        }
    }

    override fun refreshUi(fragmentName: String) {
        when (fragmentName) {
            FragmentType.PAST_MEDICAL_HISTORY_FRAGMENT -> {
              val  fragment= supportFragmentManager.findFragmentByTag(FragmentType.PAST_MEDICAL_HISTORY_FRAGMENT)
                if (fragment is PastMedicalHistoryFragment) fragment.refreshUi()
            }
            FragmentType.SURGICAL_HISTORY_FRAGMENT -> {
                val  fragment= supportFragmentManager.findFragmentByTag(FragmentType.SURGICAL_HISTORY_FRAGMENT)
                if (fragment is SurgicalHistoryFragment) fragment.refreshUi()
            }
            FragmentType.FAMILY_HISTORY_FRAGMENT -> {
                val  fragment= supportFragmentManager.findFragmentByTag(FragmentType.FAMILY_HISTORY_FRAGMENT)
                if (fragment is FamilyHistoryFragment) fragment.refreshUi()
            }
            FragmentType.ALLERGY_HISTORY_FRAGMENT -> {
                val  fragment= supportFragmentManager.findFragmentByTag(FragmentType.ALLERGY_HISTORY_FRAGMENT)
                if (fragment is AllergyHistoryFragment) fragment.refreshUi()
            }
            FragmentType.ATTACHMENT_AND_REPORT_FRAGMENT -> {
                val  fragment= supportFragmentManager.findFragmentByTag(FragmentType.ATTACHMENT_AND_REPORT_FRAGMENT)
                if (fragment is AttachmentAndReportFragment) fragment.refreshUi()
            }
        }
    }

    override fun onBackPressed() {
     //   super.onBackPressed()
        popTopMostFragment()
    }
}