package com.app.patlivecare.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        FragmentType.SPLASH_FRAGMENT,
        FragmentType.SIGN_IN_FRAGMENT,
        FragmentType.ABOUT_US_FRAGMENT,
        FragmentType.TERM_AND_CONDITION_FRAGMENT,
        FragmentType.PRIVACY_POLICY_FRAGMENT,
        FragmentType.AUTHENTICATION_FRAGMENT,
        FragmentType.FORGOT_PASSWORD_FRAGMENT,
        FragmentType.VERIFICATION_FRAGMENT,
        FragmentType.CONTACT_US_FRAGMENT,
        FragmentType.DOCTOR_SPECIALITY_FRAGMENT,
        FragmentType.BLOG_DETAIL_FRAGMENT,
        FragmentType.TOP_DOCTOR_FRAGMENT,
        FragmentType.FIND_DOCTOR_FRAGMENT,
        FragmentType.PAST_MEDICAL_HISTORY_FRAGMENT,
        FragmentType.SURGICAL_HISTORY_FRAGMENT,
        FragmentType.FAMILY_HISTORY_FRAGMENT,
        FragmentType.ALLERGY_HISTORY_FRAGMENT,
        FragmentType.ATTACHMENT_AND_REPORT_FRAGMENT,
        FragmentType.ADD_PAST_MEDICAL_HISTORY_FRAGMENT,
        FragmentType.ADD_SURGICAL_HISTORY_FRAGMENT,
        FragmentType.ADD_FAMILY_HISTORY_FRAGMENT,
        FragmentType.ADD_ALLERGY_HISTORY_FRAGMENT,
        FragmentType.ADD_ATTACHMENT_AND_REPORT_FRAGMENT,
        FragmentType.APPOINTMENT_BOOKING_FRAGMENT,
        FragmentType.DOCTOR_DETAIL_FRAGMENT,
        FragmentType.TIME_SLOT_FRAGMENT,
        FragmentType.APPOINTMENT_REQUEST_FRAGMENT,
        FragmentType.WAITING_ROOM_FRAGMENT,
        FragmentType.VIDEO_CALL_FRAGMENT,
        FragmentType.DOCTOR_RATING_FRAGMENT,
        FragmentType.DOCTOR_REVIEW_FRAGMENT,
        FragmentType.ADD_DOCTOR_REVIEW_FRAGMENT,
        FragmentType.CHANGE_PASSWORD_FRAGMENT,
        FragmentType.REPORT_FRAGMENT,
        FragmentType.CHAT_FRAGMENT,
        FragmentType.PRESCRIPTION_DETAIL_FRAGMENT,
        FragmentType.REPORT_VIEWER_FRAGMENT,
        FragmentType.MEDICAL_RECORD_FRAGMENT
})
public @interface FragmentType {
    String SPLASH_FRAGMENT = "SplashFragment";
    String SIGN_IN_FRAGMENT = "SignInFragment";
    String ABOUT_US_FRAGMENT= "AboutUsFragment";
    String TERM_AND_CONDITION_FRAGMENT= "TermAndConditionFragment";
    String PRIVACY_POLICY_FRAGMENT= "PrivacyPolicyFragment";
    String AUTHENTICATION_FRAGMENT= "AuthenticationFragment";
    String FORGOT_PASSWORD_FRAGMENT= "ForgotPasswordFragment";
    String VERIFICATION_FRAGMENT = "VerificationFragment";
    String CONTACT_US_FRAGMENT="ContactUsFragment";

    String BLOG_DETAIL_FRAGMENT="BlogDetailFragment";

    String TOP_DOCTOR_FRAGMENT="TopDoctorFragment";
    String FIND_DOCTOR_FRAGMENT="FindDoctorFragment";
    String SEARCH_DOCTOR_FRAGMENT="SearchDoctorFragment";

    String DOCTOR_SPECIALITY_FRAGMENT="DoctorSpecialityFragment";

    String MEDICAL_RECORD_FRAGMENT="MedicalRecordFragment";
    String PAST_MEDICAL_HISTORY_FRAGMENT="PastMedicalHistoryFragment";
    String SURGICAL_HISTORY_FRAGMENT="SurgicalHistoryFragment";
    String FAMILY_HISTORY_FRAGMENT="FamilyHistoryFragment";
    String ALLERGY_HISTORY_FRAGMENT="AllergyHistoryFragment";
    String ATTACHMENT_AND_REPORT_FRAGMENT="AttachmentAndReportFragment";
    String ADD_PAST_MEDICAL_HISTORY_FRAGMENT="AddPastMedicalHistoryFragment";
    String ADD_SURGICAL_HISTORY_FRAGMENT="AddSurgicalHistoryFragment";
    String ADD_FAMILY_HISTORY_FRAGMENT="AddFamilyHistoryFragment";
    String ADD_ALLERGY_HISTORY_FRAGMENT="AddAllergyHistoryFragment";
    String ADD_ATTACHMENT_AND_REPORT_FRAGMENT="AddAttachmentAndReportFragment";

    String APPOINTMENT_BOOKING_FRAGMENT="AppointmentBookingFragment";
    String DOCTOR_DETAIL_FRAGMENT="DoctorDetailFragment";
    String TIME_SLOT_FRAGMENT="TimeSlotFragment";
    String APPOINTMENT_REQUEST_FRAGMENT="AppointmentRequestFragment";
    String WAITING_ROOM_FRAGMENT="WaitingRoomFragment";
    String VIDEO_CALL_FRAGMENT="VideoCallFragment";
    String DOCTOR_RATING_FRAGMENT="DoctorRatingFragment";
    String DOCTOR_REVIEW_FRAGMENT="DoctorReviewFragment";
    String ADD_DOCTOR_REVIEW_FRAGMENT="AddDoctorReviewFragment";
    String REPORT_FRAGMENT="ReportFragment";
    String CHANGE_PASSWORD_FRAGMENT="ChangePasswordFragment";
    String CHAT_FRAGMENT="ChatFragment";
    String PRESCRIPTION_DETAIL_FRAGMENT="PrescriptionDetailFragment";
    String REPORT_VIEWER_FRAGMENT="ReportViewerFragment";

}
    // Declare the constants
