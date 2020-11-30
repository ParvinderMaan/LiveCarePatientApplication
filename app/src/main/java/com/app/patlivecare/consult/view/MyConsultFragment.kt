package com.app.patlivecare.consult.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.app.patlivecare.R
import kotlinx.android.synthetic.main.fragment_my_consult.*
import kotlinx.android.synthetic.main.fragment_upcoming_appointment.*

class MyConsultFragment : Fragment() {
    private lateinit var upcomingAppointmentFragment: UpcomingAppointmentFragment
    private lateinit var pastAppointmentFragment: PastAppointmentFragment

    companion object {
        fun newInstance() = MyConsultFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        upcomingAppointmentFragment = UpcomingAppointmentFragment.newInstance()
        pastAppointmentFragment = PastAppointmentFragment.newInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_consult, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_upcoming_consult.isSelected=true
        tv_past_consult.isSelected=false
        showUpcomingAppointmentFragment()
        tv_upcoming_consult?.setOnClickListener {
            tv_upcoming_consult.isSelected=true
            tv_past_consult.isSelected=false
            showUpcomingAppointmentFragment()
        }
        tv_past_consult?.setOnClickListener {
            tv_upcoming_consult.isSelected=false
            tv_past_consult.isSelected=true
            showPastAppointmentFragment()
        }
        val inAnim: Animation = AnimationUtils.loadAnimation(activity,R.anim.enter_from_right)
        val outAnim: Animation = AnimationUtils.loadAnimation(activity,R.anim.exit_to_left)
        view_switcher?.inAnimation = inAnim
        view_switcher?.outAnimation = outAnim
    }

    private fun showUpcomingAppointmentFragment() {
        val ft=childFragmentManager.beginTransaction()
        if (upcomingAppointmentFragment.isAdded) ft.show(upcomingAppointmentFragment)
        else ft.add(R.id.fl_container, upcomingAppointmentFragment)
        if (pastAppointmentFragment.isAdded) ft.hide(pastAppointmentFragment)
        ft.commit()
    }

    private fun showPastAppointmentFragment() {
        val ft=childFragmentManager.beginTransaction()
        if (pastAppointmentFragment.isAdded) ft.show(pastAppointmentFragment)
        else ft.add(R.id.fl_container, pastAppointmentFragment)
        if (upcomingAppointmentFragment.isAdded) ft.hide(upcomingAppointmentFragment)
        ft.commit()
    }
}