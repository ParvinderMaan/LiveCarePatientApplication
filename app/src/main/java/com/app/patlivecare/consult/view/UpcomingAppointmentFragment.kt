package com.app.patlivecare.consult.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.extra.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.AppointmentStatus
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.consult.model.AppointmentInfo
import com.app.patlivecare.consult.viewmodel.UpcomingAppointmentViewModel
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import kotlinx.android.synthetic.main.fragment_upcoming_appointment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs


class UpcomingAppointmentFragment : BaseFragment() {
    private lateinit var todayZonedDateTime: ZonedDateTime
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private  lateinit var headerMap: HashMap<String, String>
    private lateinit var viewModel: UpcomingAppointmentViewModel
    private var confirmedAdapter: UpcomingAppointmentAdapter? = null
    private var pendingAdapter: UpcomingAppointmentAdapter? = null
    private var inAnimPending: Animation? = null
    private var outAnimPending: Animation? = null
    private var inAnimConfirm: Animation? = null
    private var outAnimConfirm: Animation? = null
    private  var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance() = UpcomingAppointmentFragment()
    }

    override fun getRootView(): View {
        return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
         headerMap = HashMap()
         headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
         headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken

        confirmedAdapter=UpcomingAppointmentAdapter()
        pendingAdapter=UpcomingAppointmentAdapter()

        pendingAdapter?.setOnItemClickListener(pendingItemClickListener)
        confirmedAdapter?.setOnItemClickListener(confirmItemClickListener)

        inAnimPending = AnimationUtils.loadAnimation(activity, R.anim.enter_from_right)
        outAnimPending = AnimationUtils.loadAnimation(activity, R.anim.exit_to_left)
        inAnimConfirm = AnimationUtils.loadAnimation(activity, R.anim.enter_from_left)
        outAnimConfirm= AnimationUtils.loadAnimation(activity, R.anim.exit_to_right)

        todayZonedDateTime=ZonedDateTime.now()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upcoming_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UpcomingAppointmentViewModel::class.java)
        viewModel.headerMap=headerMap
        viewModel.fetchUpcomingAppointment(AppointmentStatus.CONFIRMED)
        viewModel.fetchUpcomingAppointment(AppointmentStatus.PENDING)

        initRecyclerView()

        viewModel.showWhichView.value=1
        tv_confirmed_consult?.setOnClickListener {
            viewModel.showWhichView.value=1
            view_switcher?.showNext()
        }
        tv_pending_consult?.setOnClickListener {
            viewModel.showWhichView.value=2
            view_switcher?.showPrevious()
        }

        swipe_refresh_confirmed_consult?.setOnRefreshListener {
            viewModel.fetchUpcomingAppointment(AppointmentStatus.CONFIRMED)
        }
        swipe_refresh_pending_consult?.setOnRefreshListener {
            viewModel.fetchUpcomingAppointment(AppointmentStatus.PENDING)
        }
        initObserver()
    }

    private fun initRecyclerView() {
        rv_confirmed_consult?.apply {
            layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = confirmedAdapter
        }
        rv_pending_consult?.apply {
            layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = pendingAdapter
        }

        swipe_refresh_confirmed_consult?.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        swipe_refresh_pending_consult?.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) progress_bar?.visibility = View.VISIBLE
            else {
                progress_bar?.visibility = View.INVISIBLE
                swipe_refresh_confirmed_consult?.isRefreshing = false
                swipe_refresh_pending_consult?.isRefreshing = false
            }
        })
        viewModel.resultAppointmentConfirmed.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.data?.let {
                        viewLifecycleOwner.lifecycleScope.launch {
                            getFlow(it)
                                .flowOn(Dispatchers.Default)
                                .collect { result ->
                                    viewModel.lstOfConfirmedAppmnt.value = result
                                }
                        }
                    }
                }
                Status.FAILURE -> {
                    showSnackBar(it.errorMsg.toString())
                }
            }
        })
        viewModel.resultAppointmentPending.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.data?.let {
                        viewLifecycleOwner.lifecycleScope.launch {
                            getFlow(it)
                                .flowOn(Dispatchers.Default)
                                .collect { result ->
                                    viewModel.lstOfPendingAppmnt.value = result
                                }
                        }
                    }
                }
                Status.FAILURE -> {
                    showSnackBar(it.errorMsg.toString())
                }
            }
        })
        viewModel.resultPendingAppnmtCancel.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.message?.let { msg ->
                        val tempList = pendingAdapter?.currentList?.toMutableList()
                        if (!tempList.isNullOrEmpty() && viewModel.itemToBeDeleted != -1) {
                            tempList.removeAt(viewModel.itemToBeDeleted)
                            viewModel.lstOfPendingAppmnt.value = tempList
                            viewModel.itemToBeDeleted = -1
                            showSnackBar(msg, R.color.colorGreen)
                        }
                    }
                }
                Status.FAILURE -> {
                    showSnackBar(it.errorMsg.toString())
                    viewModel.itemToBeDeleted = -1
                }
            }
        })
        viewModel.resultConfirmAppnmtCancel.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.message?.let { msg ->
                        val tempList = confirmedAdapter?.currentList?.toMutableList()
                        if (!tempList.isNullOrEmpty() && viewModel.itemToBeDeleted != -1) {
                            tempList.removeAt(viewModel.itemToBeDeleted)
                            viewModel.lstOfConfirmedAppmnt.value = tempList
                            viewModel.itemToBeDeleted = -1
                            showSnackBar(msg, R.color.colorGreen)
                        }
                    }
                }
                Status.FAILURE -> {
                    viewModel.itemToBeDeleted = -1
                    showSnackBar(it.errorMsg.toString())
                }
            }
        })
        viewModel.lstOfConfirmedAppmnt.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                iv_empty_confirm?.visibility = View.VISIBLE
                tv_empty_confirm?.visibility = View.VISIBLE
            } else {
                confirmedAdapter?.submitList(it)
            }

        })
        viewModel.lstOfPendingAppmnt.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                pendingAdapter?.submitList(null)
                iv_empty_pending?.visibility = View.VISIBLE
                tv_empty_pending?.visibility = View.VISIBLE
            } else {
                pendingAdapter?.submitList(it)
            }
        })
        viewModel.showWhichView.observe(viewLifecycleOwner, Observer {
            when (it) {
                1 -> {
                    view_switcher?.inAnimation = inAnimConfirm
                    view_switcher?.outAnimation = outAnimConfirm
                    // view_switcher?.showNext()
                    tv_pending_consult.isSelected = false
                    tv_confirmed_consult.isSelected = true
                    //    tv_confirmed_consult?.paintFlags = tv_confirmed_consult.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    //    tv_pending_consult?.paintFlags=0
                }

                2 -> {
                    view_switcher?.inAnimation = inAnimPending
                    view_switcher?.outAnimation = outAnimPending
                    tv_pending_consult.isSelected = true
                    tv_confirmed_consult.isSelected = false
                    //   tv_pending_consult?.paintFlags = tv_pending_consult.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    //   tv_confirmed_consult?.paintFlags=0
                }
            }

        })
    }


    private val pendingItemClickListener:UpcomingAppointmentAdapter.OnItemClickListener= object :
            UpcomingAppointmentAdapter.OnItemClickListener {
            override fun onItemClick(item: AppointmentInfo?) {}

            override fun onItemMoreClick(
                item: AppointmentInfo?,
                adapterPosition: Int,
                view: ImageButton
            ) {
                val popupMenu = PopupMenu(view.context, view)
                val menu = popupMenu.menu
                menu.add(0, 1, 0, getString(R.string.action_cancel_request))
                menu.add(0, 2, 0, getString(R.string.action_view_profile))
                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        1 -> {
                            // cancel the request ........
                            viewModel.itemToBeDeleted = adapterPosition
                            viewModel.attemptCancelAppointment(
                                item!!.appointmentId,
                                AppointmentStatus.CANCELLED
                            )
                        }
                        2 -> {
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
                    }
                    false
                }
                popupMenu.show()
            }
        }

    private val confirmItemClickListener:UpcomingAppointmentAdapter.OnItemClickListener= object :
        UpcomingAppointmentAdapter.OnItemClickListener {
        override fun onItemClick(item: AppointmentInfo?) {
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.WAITING_ROOM_FRAGMENT)
            intent.putExtra("key_", item)
            startActivity(intent)
        }
        override fun onItemMoreClick(item: AppointmentInfo?, adapterPosition: Int, view: ImageButton) {
            val popupMenu = PopupMenu(view.context, view)
            val menu = popupMenu.menu
            menu.add(0, 1, 0, getString(R.string.action_pay_now))
            menu.add(0, 2, 0, getString(R.string.action_chat))
            menu.add(0, 3, 0, getString(R.string.action_cancel_appointment))
            menu.add(0, 4, 0, getString(R.string.action_view_profile))
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    1 -> {
                        // show pay now screen....
                    }
                    2 -> {
                        val intent = Intent(activity, MinorActivity::class.java)
                        intent.putExtra("fragment_type", FragmentType.CHAT_FRAGMENT)
                        startActivity(intent)
                    }
                    3 -> {
                        // cancel the request ........
                        viewModel.itemToBeDeleted = adapterPosition
                        viewModel.attemptCancelAppointment(item!!.appointmentId, AppointmentStatus.CANCELLED)
                    }
                    4 -> {
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
                }
                false
            }
            popupMenu.show()
        }
    }

    private suspend fun getFlow(lstOfAppointments: List<AppointmentInfo>): Flow<List<AppointmentInfo>> {
        return flow {
            val list = lstOfAppointments.asFlow()
                .map {
                    val outFormatter = DateTimeFormatter.ofPattern(
                        "dd-MM-yyyy HH:mm:ss",
                        Locale.getDefault())
                    val dateTimeFrom = LocalDateTime.parse(it.slotFrom, outFormatter)
                        .atOffset(ZoneOffset.UTC)
                        .atZoneSameInstant(ZoneId.systemDefault())
                    val formattedDateFrom: String = dateTimeFrom.format(outFormatter)
                    it.slotFrom=formattedDateFrom

                    val dateTimeTo = LocalDateTime.parse(it.slotTo, outFormatter)
                        .atOffset(ZoneOffset.UTC)
                        .atZoneSameInstant(ZoneId.systemDefault())
                    val formattedDateTo: String = dateTimeTo.format(outFormatter)
                    it.slotTo=formattedDateTo

                    if(todayZonedDateTime.isBefore(dateTimeFrom)){
                        val duration = Duration.between(dateTimeFrom, todayZonedDateTime)
                        val diff = abs(duration.toMillis())
                        it.remainingTime=diff
                    }else{
                        it.remainingTime=-1
                    }
                    return@map it
                }
                .toList()

            emit(list)
        }

    }
}