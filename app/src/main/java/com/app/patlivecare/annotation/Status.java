package com.app.patlivecare.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
@IntDef ({ Status.FAILURE, Status.SUCCESS})
public @interface Status {
    int FAILURE = 0;
    int SUCCESS = 1;
}