package com.app.patlivecare.appointment.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.appointment.model.TimeSlotInfo
import com.app.patlivecare.appointment.viewmodel.TimeSlotViewModel
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_time_slot.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class TimeSlotFragment : BaseFragment() {
    private lateinit var zonedDateTime:ZonedDateTime
    private  var mFragmentListener: HomeFragmentSelectedListener? = null
    private  var mornAdapter: TimeSlotAdapter?=null
    private  var evenAdapter: TimeSlotAdapter?=null
    private  var doctorInfo: DoctorInfo? = null
    private  var sharedPrefs: SharedPrefHelper? = null
    private  lateinit var viewModel: TimeSlotViewModel

    companion object {
        fun newInstance(payload: Any?): TimeSlotFragment {
            val fragment = TimeSlotFragment()
            val bundle = Bundle()
            if (payload is Parcelable) bundle.putParcelable("KEY", payload)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getRootView(): View {
       return cl_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentSelectedListener) mFragmentListener = context
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mornAdapter= TimeSlotAdapter()
        evenAdapter= TimeSlotAdapter()
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel = ViewModelProvider(this).get(TimeSlotViewModel::class.java)
        viewModel.headerMap = headerMap

        arguments?.let {
            doctorInfo = it.getParcelable("KEY")
        }

//        if(doctorInfo?.id !=null && doctorInfo?.date!=null){
//            viewModel.fetchDoctorTimeSlot(doctorInfo!!.id,doctorInfo?.date?:"")
//        }

        if(doctorInfo?.id !=null && doctorInfo?.date!=null){
             val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
             val selDateTime = LocalDateTime.parse(doctorInfo?.date, dtf)
             zonedDateTime = selDateTime.atZone(ZoneId.systemDefault())
             val utcDateTime:ZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)
         //    val prevDate = currDate.minusDays(1)
         //    val nextDate = currDate.plusDays(1)
             //dtf.format(prevDate), dtf.format(currDate), dtf.format(nextDate)
            val dtfOut = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            viewModel.fetchDoctorTimeSlot(doctorInfo!!.id,dtfOut.format(utcDateTime))
             //    Log.e("local : ",dtf.format(zonedDateTime))
             //    Log.e("utc : ", dtf.format(utcDateTime))
            //manipulating data ....................
            val dtff = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val localDate = LocalDateTime.parse(doctorInfo!!.date,DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            doctorInfo!!.date=dtff.format(localDate)
          }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_slot, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_select_time_slot)
        if(activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(
            toolbar_main
        )

        rv_time_slot_morn?.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter=mornAdapter
        }
        rv_time_slot_even?.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter=evenAdapter
        }


        mornAdapter?.setOnItemClickListener(object : TimeSlotAdapter.OnItemClickListener {
            override fun onItemClick(model: TimeSlotInfo, adapterPosition: Int) {
                doctorInfo?.timeSlotInfo = model  // adding extra value
                mFragmentListener?.showFragment(
                    FragmentType.APPOINTMENT_REQUEST_FRAGMENT,
                    doctorInfo
                )
            }
        })

        evenAdapter?.setOnItemClickListener(object :
            TimeSlotAdapter.OnItemClickListener {
            override fun onItemClick(model: TimeSlotInfo, adapterPosition: Int) {
                doctorInfo?.timeSlotInfo = model  // adding extra value
                mFragmentListener?.showFragment(
                    FragmentType.APPOINTMENT_REQUEST_FRAGMENT,
                    doctorInfo
                )
            }
        })

        initView()
        initListener()
        initObserver()

        val into: Animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left)
        view_switcher?.inAnimation = into
    }

    private fun initListener() {
        iv_previous?.setOnClickListener {
            viewModel.showSlots.value=1
        }

        iv_forward?.setOnClickListener {
            viewModel.showSlots.value=2
        }

        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }

        cardview_one?.setOnClickListener{}
        cardview_two?.setOnClickListener{}
    }

    @ExperimentalStdlibApi
    private fun initView() {

        doctorInfo?.let {
            if (!it.profilePic.isNullOrEmpty()) {
                Glide.with(this)
                    .load(WebUrl.BASE_FILE + it.profilePic)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.colorLightestGrey)
                    .into(iv_profile)
            } else {
                Glide.with(this)
                    .load(R.drawable.img_avatar)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv_profile)
            }

            val fullName = it.name.split(" ").toMutableList()
            var output = ""
            for (name in fullName) {
                output += name.capitalize(Locale.getDefault()) + " "
            }
            tv_doc_name?.text = output
            if (it.doctorSpecialities.isNotEmpty()) tv_speciality?.text =
                it.doctorSpecialities[0].name

             it.date?.let {
                tv_date.text=it

             }
        }
    }



private fun initObserver() {
    viewModel.isLoading.observe(viewLifecycleOwner, Observer {
        if (it) progress_bar?.visibility = View.VISIBLE
        else progress_bar?.visibility = View.INVISIBLE
    })

    viewModel.showSlots.observe(viewLifecycleOwner, Observer {
        when (it) {
            1 -> {
                tv_session.text = getString(R.string.title_morning)
                tv_session?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_morning, 0, 0, 0);
                view_switcher?.showPrevious()
            }
            2 -> {
                tv_session.text = getString(R.string.title_evening)
                tv_session?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_evening, 0, 0, 0)
                view_switcher?.showNext()
            }
        }

    })

    viewModel.resultDoctorTimeSlot.observe(viewLifecycleOwner, Observer {
        when (it.status) {
            Status.SUCCESS -> {
                it.data?.data?.let { lstOfTimeSlots ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        getTimeSlotFlow(lstOfTimeSlots)
                            .flowOn(Dispatchers.Default)
                            .collect { result ->
                                viewModel.lstOfTimeSlots.value = lstOfTimeSlots
                            }
                    }
                }
            }
            Status.FAILURE -> {
                showSnackBar(it.errorMsg.toString())
            }
        }
    })

    viewModel.lstOfTimeSlots.observe(viewLifecycleOwner, Observer {
        if (it.isEmpty()) {
            group_empty_view?.visibility = View.VISIBLE
            group_content?.visibility = View.INVISIBLE
        } else {
            group_empty_view?.visibility = View.INVISIBLE
            group_content?.visibility = View.VISIBLE

            val morningLst = it.filter { item -> item.slotFrom.contains("am") || item.slotFrom.contains("AM") } //PM
            val eveningLst = it.filter { item -> item.slotFrom.contains("pm") || item.slotFrom.contains("PM")}
            if (eveningLst.isEmpty()) {
                iv_empty_even?.visibility = View.VISIBLE
                tv_empty_text_?.visibility = View.VISIBLE
            } else {
                evenAdapter?.submitList(eveningLst)
            }

            if (morningLst.isEmpty()) {
                iv_empty_morn?.visibility = View.VISIBLE
                tv_empty_text?.visibility = View.VISIBLE
            } else {
                mornAdapter?.submitList(morningLst)
            }
        }
    })
}

    private suspend fun getTimeSlotFlow(lstOfTimeSlot: List<TimeSlotInfo>): Flow<List<TimeSlotInfo>> {
        return flow {
            val list = lstOfTimeSlot.asFlow()
                .map {  // utc ---> localtime
                    val outFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                    val dateTime = LocalDateTime.parse(it.slotFrom, outFormatter)
                        .atOffset(ZoneOffset.UTC)
                        .atZoneSameInstant(ZoneId.systemDefault())
                    it.slotFrom=dateTime.format(outFormatter)
                    return@map it
                }
                .filter {
                    val inFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                    val outFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
                    val dateTimeIn = LocalDate.parse(it.slotFrom, inFormatter)
                    val formattedDate: String =dateTimeIn.format(outFormatter)
                    val dateTimeOut = LocalDate.parse(formattedDate, outFormatter)
                    val diffX: Int = dateTimeOut.compareTo(zonedDateTime.toLocalDate())
                 //    Log.e("diffX : ", it.timeSlotId.toString()+" "+diffX)
                    if (diffX==0) {
                        return@filter true
                    }
                    return@filter false
                }
                .map {
                    val inFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault()) // 01-09-2020 ---> 1-9-2020
                    val outFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()) // 01-09-2020 ---> 1-9-2020
                    val dateTime = LocalDateTime.parse(it.slotFrom, inFormatter)
                    val formattedDate: String =dateTime.format(outFormatter)
                    it.slotFrom=formattedDate
                    return@map it
                }
                .toList()

            emit(list)
        }

    }


}