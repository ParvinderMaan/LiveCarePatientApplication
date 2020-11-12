package com.app.patlivecare.annotation;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
@StringDef({ SignInType.GENERAL, SignInType.FACEBOOK,SignInType.GOOGLE})
public @interface SignInType {
    String GENERAL = "Email";
    String FACEBOOK = "Facebook";
    String GOOGLE = "Google";

}
