package com.app.patlivecare.annotation;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
@IntDef({AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED,AppointmentStatus.CANCELLED})
public @interface AppointmentStatus {
    int PENDING = 0;
    int CONFIRMED = 1;
    int CANCELLED =3;
}
