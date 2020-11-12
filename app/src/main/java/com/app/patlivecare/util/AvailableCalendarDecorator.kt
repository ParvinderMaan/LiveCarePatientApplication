package com.app.patlivecare.util

import android.content.res.Resources
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.app.patlivecare.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class AvailableCalendarDecorator(private var date: String?, private var resource: Resources?) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        val date = day?.day.toString() + "-" + day?.month + "-" + day?.year
            // Log.e("AvailableCalendarDecorator", "" + date);
        return this.date.equals(date)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setDaysDisabled(false)
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#FFFFFF"))) // White
        resource?.getDrawable(R.drawable.bg_decorator_available, null)?.let {
            view?.setBackgroundDrawable(it) }
    }
}