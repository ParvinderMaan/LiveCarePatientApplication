package com.app.patlivecare.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ Verification.EMAIL, Verification.PHONE})
public @interface Verification {
    int EMAIL = 1;
    int PHONE = 2;
}
