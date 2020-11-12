package com.app.patlivecare.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ Gender.MALE, Gender.FEMALE,Gender.OTHERS})
public @interface Gender {
    int MALE = 1;
    int FEMALE = 2;
    int OTHERS = 3;
}