package com.app.patlivecare.appointment.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeSlotInfo (var isSlotAvailable: Boolean,
                         var slotFrom: String,
                         var slotTo: String,
                         var timeSlotId: Int,
                         var date: String): Parcelable