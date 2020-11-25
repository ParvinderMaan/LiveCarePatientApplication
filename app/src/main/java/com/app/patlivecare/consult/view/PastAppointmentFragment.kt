package com.app.patlivecare.consult.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.extra.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.consult.viewmodel.PastAppointmentViewModel
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_past_appointment.*
import java.util.HashMap


class PastAppointmentFragment : Fragment() {
    private var sharedPrefs: SharedPrefHelper? = null
    private lateinit var viewModel: PastAppointmentViewModel
    private var pastAdapter : PastAppointmentAdapter?=null

    companion object {
        fun newInstance() = PastAppointmentFragment()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pastAdapter = PastAppointmentAdapter()
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer "+accessToken
        viewModel = ViewModelProvider(this).get(PastAppointmentViewModel::class.java)
        viewModel.headers= headerMap
        viewModel.fetchPastAppointments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_past_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObserver()
    }

    private fun initRecyclerView() {
        rv_past_appointment?.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = pastAdapter
            // pastAppointmentAdapter.addAll(getDummyData())
        }
        pastAdapter?.setOnItemClickListener(object :
            PastAppointmentAdapter.OnItemClickListener {
            override fun onItemClick(item: AppointmentInfo?) {
                // do nothing......


            }

            override fun onItemMoreClick(view: ImageButton, item: AppointmentInfo) {
//              val popupMenu = PopupMenu(view.context,view, Gravity.CENTER ,0, R.style.PopupMenuMoreCentralized)
                val popupMenu = PopupMenu(view.context,view)

                val menu = popupMenu.menu
                menu.add(0, 1, 0, getString(R.string.action_view_profile))
                menu.add(0, 2, 0, getString(R.string.action_rating))
                menu.add(0, 3, 0, getString(R.string.action_add_review))
                menu.add(0, 4, 0, getString(R.string.action_report))

                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        1 ->{
                            // view profile .....
                            val fullName = (item!!.firstName).plus(" ").plus(item.lastName)
                            val doctorInfo = DoctorInfo(
                                id = item.doctorId,
                                name = fullName,
                                doctorSpecialities = item.doctorSpecialities,
                                profilePic = item.profilePic
                            )

                            val intent = Intent(activity, MinorActivity::class.java)
                            intent.putExtra("key_", doctorInfo)
                            intent.putExtra("fragment_type", FragmentType.DOCTOR_DETAIL_FRAGMENT)
                            startActivity(intent)

                        }

                        2 -> {
                            val intent= Intent(activity, MinorActivity::class.java)
                            intent.putExtra("key_", item)
                            intent.putExtra("fragment_type", FragmentType.DOCTOR_RATING_FRAGMENT)
                            startActivity(intent)

                        }
                        3 -> {
                            val intent= Intent(activity, MinorActivity::class.java)
                            intent.putExtra("key_", item)
                            intent.putExtra("fragment_type", FragmentType.ADD_DOCTOR_REVIEW_FRAGMENT)
                            startActivity(intent)

                        }
                        4 -> {
                            ///.....................Report screen missing !!!
                            val intent= Intent(activity, MinorActivity::class.java)
                            intent.putExtra("fragment_type", FragmentType.REPORT_FRAGMENT)
                            startActivity(intent)
                        }
                    }
                    false
                }
                popupMenu.show()
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
            pastAdapter?.submitList(it)
        })

    }
}