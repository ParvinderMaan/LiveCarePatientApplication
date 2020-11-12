package com.app.patlivecare.util

import android.content.res.Resources
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.app.patlivecare.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class UnAvailableCalendarDecorator (private var date: String?, private var resource: Resources?) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        val date = day?.day.toString() + "-" + day?.month + "-" + day?.year
        return this.date.equals(date)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setDaysDisabled(true)
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#CC262626"))) // White
        resource?.getDrawable(R.drawable.bg_decorator_unavailable, null)?.let {
            view?.setBackgroundDrawable(it) }
    }

}