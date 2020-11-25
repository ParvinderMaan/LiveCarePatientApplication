package com.app.patlivecare.appointment.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.Status
import com.app.patlivecare.appointment.model.DoctorScheduleResponse
import com.app.patlivecare.appointment.viewmodel.AppointmentBookingViewModel
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.doctor.model.DoctorInfo
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.interfacor.HomeFragmentSelectedListener
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.network.WebUrl
import com.app.patlivecare.util.AvailableCalendarDecorator
import com.app.patlivecare.util.UnAvailableCalendarDecorator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import kotlinx.android.synthetic.main.fragment_appointment_booking.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList


class AppointmentBookingFragment : BaseFragment() {
    private var mFragmentListener: HomeFragmentSelectedListener? = null
    private lateinit var viewModel: AppointmentBookingViewModel
    private lateinit var minDateOfCalendar: LocalDate
    private lateinit var maxDateOfCalendar: LocalDate
    private lateinit var zonedDateTime: ZonedDateTime
    private var doctorInfo: DoctorInfo? = null
    private var sharedPrefs: SharedPrefHelper? = null

    companion object {
        fun newInstance(payload: Any?): AppointmentBookingFragment {
            val fragment = AppointmentBookingFragment()
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
        zonedDateTime = ZonedDateTime.now()
        val tempDateOfCalendar = LocalDate.of(zonedDateTime.year, zonedDateTime.month.plus(1), 1)
        minDateOfCalendar = LocalDate.of(zonedDateTime.year, zonedDateTime.month, 1)
        maxDateOfCalendar = LocalDate.of(tempDateOfCalendar.year, tempDateOfCalendar.month, tempDateOfCalendar.lengthOfMonth())
        arguments?.let { doctorInfo = it.getParcelable("KEY") }
        val accessToken = sharedPrefs?.read(SharedPrefHelper.KEY_ACCESS_TOKEN, "").toString()
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        headerMap[WebHeader.KEY_AUTHORIZATION] = "Bearer " + accessToken
        viewModel = ViewModelProvider(this).get(AppointmentBookingViewModel::class.java)
        viewModel.headerMap = headerMap
        val dtFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        doctorInfo?.id?.let{
          val dateOfMonth = dtFormatter.format(ZonedDateTime.now(ZoneOffset.UTC).toLocalDate())
          viewModel.fetchDoctorSchedule(it, dateOfMonth)
       }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_appointment_booking, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title?.text = getString(R.string.title_book_an_appointment)
        if (activity is AppCompatActivity) (activity as AppCompatActivity).setSupportActionBar(toolbar_main)
        ibtn_close?.setOnClickListener {
            mFragmentListener?.popTopMostFragment()
        }
        cardview_header?.setOnClickListener {} // ripple effect
        cardview_footer?.setOnClickListener {} // ripple effect
        initCalendarView()
        initView()
        initObserver()
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

        }

    }

    private fun initCalendarView() {
        cal_view_schedule?.setTitleMonths(R.array.label_months)
        cal_view_schedule?.setTitleFormatter(object : DateFormatTitleFormatter() {
            override fun format(day: CalendarDay?): CharSequence {
                val dateFormatter = DateTimeFormatter.ofPattern("MMMM")
                return dateFormatter.format(day?.date)
            }
        })
        cal_view_schedule?.state()?.edit()
            ?.setFirstDayOfWeek(DayOfWeek.SUNDAY)
            ?.setMinimumDate(minDateOfCalendar)
            ?.setShowWeekDays(true)
            ?.setCalendarDisplayMode(CalendarMode.MONTHS)
            ?.setMaximumDate(maxDateOfCalendar)
            ?.commit()

        cal_view_schedule?.topbarVisible = true
        cal_view_schedule?.setOnDateChangedListener(OnDateSelectedListener { widget: MaterialCalendarView?, day: CalendarDay, selected: Boolean -> })
        cal_view_schedule?.setWeekDayFormatter(WeekDayFormatter { dayOfWeek: DayOfWeek ->
            dayOfWeek.getDisplayName(
                TextStyle.NARROW,
                Locale.US
            )
        })


        //  cal_view_schedule?.addDecorators(lstOfDays) // for now !!!
        cal_view_schedule?.setOnDateChangedListener { widget, date_, selected ->
            if(doctorInfo!=null) {
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val selDateOfMonth = date_.date
                val localDateTime: LocalDateTime = selDateOfMonth.atStartOfDay()
                doctorInfo?.date=formatter.format(localDateTime)
                mFragmentListener?.showFragment(FragmentType.TIME_SLOT_FRAGMENT, doctorInfo)
            }

        }
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) progress_bar?.visibility = View.VISIBLE
            else progress_bar?.visibility = View.INVISIBLE
        })
        viewModel.resultDoctorSchedule.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.data?.let { lstOfAvailability ->
                        viewModel.lstOfAvailableDates.value = lstOfAvailability
                    }
                }
                Status.FAILURE -> {
                    showSnackBar(it.errorMsg.toString())
                }
            }
        })
        viewModel.lstOfAvailableDates.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                group_empty_view?.visibility = View.VISIBLE
                group_content?.visibility = View.INVISIBLE
            } else {
                group_empty_view?.visibility = View.INVISIBLE
                group_content?.visibility = View.VISIBLE
                viewLifecycleOwner.lifecycleScope.launch {
                    getUnAvailableDaysFlow().zip(getAvailableDaysFlow(it)) { lstOfNonAvailableDays, lstOfAvailableDays ->
                        val result: ArrayList<DayViewDecorator> = ArrayList()
                        result.addAll(lstOfNonAvailableDays)
                        result.addAll(lstOfAvailableDays)
                        return@zip result
                    }.flowOn(Dispatchers.IO)
                        .collect {
                            cal_view_schedule?.addDecorators(it)
                        }
                }
            }
        })
    }

    private fun getUnAvailableDaysFlow(): Flow<List<DayViewDecorator>> {
        return flow{
            val lstOfDays: MutableList<DayViewDecorator> = ArrayList()
            val formatter = DateTimeFormatter.ofPattern("d-M-yyyy")
            var date = minDateOfCalendar
            while (date.isBefore(maxDateOfCalendar.plusDays(1))) { // .plusDays(1) may cause issue ???
                val dateModified = formatter.format(date)
                lstOfDays.add(UnAvailableCalendarDecorator(dateModified, resources))
                date = date.plusDays(1)
            }
            emit(lstOfDays)
        }
    }
    private suspend fun getAvailableDaysFlow(days: List<DoctorScheduleResponse.AvailableDate>): Flow<List<DayViewDecorator>> {
        return flow{
            val list = days.asFlow()
                .map { item->
                    val outFormatter = DateTimeFormatter.ofPattern("d-M-yyyy HH:mm:ss", Locale.getDefault()) // 01-09-2020 ---> 1-9-2020
                    val dateTime = LocalDateTime.parse(item.fromTime, outFormatter)
                        .atOffset(ZoneOffset.UTC)
                        .atZoneSameInstant(ZoneId.systemDefault())
                    return@map dateTime
                }
                .filter {
                    val diff: Int = it.toLocalDate().compareTo(zonedDateTime.toLocalDate())
                    if (diff > 0 || diff == 0) {   // include / exclude current date with || diff == 0
                        return@filter true
                    }
                    return@filter false
                }
                .map {
                    val formattedDate: String = it.format(DateTimeFormatter.ofPattern("d-M-yyyy"))
                    return@map AvailableCalendarDecorator(formattedDate, resources)
                }
                .distinctUntilChanged()
                .toList()
            emit(list)
        }
    }

}
