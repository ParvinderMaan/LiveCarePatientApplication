package com.app.patlivecare.network

/*
  this class has private constructor
 in order to restrict its object creation
 it contains all the url used for network request
*/
class WebUrl private constructor() {
    companion object {
        // base urls
        const val BASE = "https://livecare24.azurewebsites.net/api/"    // https://api.stackexchange.com/
        const val BASE_FILE = "https://livecare24.azurewebsites.net/"
        // child urls
        const val SIGN_UP = "Auth/PatientRegister"
        const val SIGN_IN = "Auth/PatientLogin"
        const val SIGN_OUT = "Auth/Logout"

        const val SOCIAL_SIGN_IN = "Auth/PatientSocialLogin"

        const val BASIC_PROFILE_INFO = "Patient/GetPatientBasicInfo"
        const val ADDITIONAL_PROFILE_INFO = "Patient/GetPatientAdditionalInfo"
        const val ALTER_PROFILE_PIC = "Auth/UpdateProfilePic"
        const val UPDATE_BASIC_PROFILE_INFO = "Patient/UpdatePatientBasicInfo"
        const val UPDATE_ADDITIONAL_PROFILE_INFO = "Patient/AddPatientHealthInfo"

        const val FETCH_COUNTRY = "Content/GetCountries"
        const val FETCH_STATE = "Content/GetStates"

        const val VERIFY_EMAIL = "Auth/VerifyEmail"
        const val SEND_EMAIL_OTP = "Auth/ResendEmailCode"

        const val VERIFY_PHONE = "Auth/VerifyPhone"
        const val SEND_PHONE_OTP = "Auth/ResendPhoneOtp"


        // password
        const val FORGOT_PASSWORD = "Auth/ForgotPassword"
        const val RESET_PASSWORD = "Auth/ResetPassword"
        const val CHANGE_PASSWORD = "Auth/ChangePassword"

        // settings
        const val EN_DIS_NOTIFICATION = "Content/UpdateNotificationStatus"
        const val FETCH_NOTIFICATIONS = "notifications"
        const val DELETE_ALL_NOTIFICATIONS = ""
        const val ABOUT_US = "page-details"
        const val TERMS_AND_CONDITIONS = "page-details"
        const val CONTACT_US = "Content/ContactUs"

        // medical records
        const val ADD_PAST_MEDICAL_HISTORY = "MedicalHistory/AddPastMedicalHistory"
        const val DELETE_PAST_MEDICAL_HISTORY = "MedicalHistory/DeletePastMedicalHistory"
        const val FETCH_PAST_MEDICAL_HISTORY = "MedicalHistory/GetPastMedicalHistory"

        const val ADD_SURGICAL_HISTORY = "MedicalHistory/AddPatientSurgicalHistory"
        const val DELETE_SURGICAL_HISTORY = "MedicalHistory/DeleteSurgicalHistory"
        const val FETCH_SURGICAL_HISTORY = "MedicalHistory/GetPatientSurgicalHistory"

        const val ADD_FAMILY_HISTORY = "MedicalHistory/AddPatientFamilyHistory"
        const val DELETE_FAMILY_HISTORY = "MedicalHistory/DeleteFamilyHistory"
        const val FETCH_FAMILY_HISTORY = "MedicalHistory/GetPatientFamilyHistory"

        const val ADD_ALLERGY_HISTORY = "MedicalHistory/AddPatientAllergyHistory"
        const val DELETE_ALLERGY_HISTORY = "MedicalHistory/DeleteAllergyHistory"
        const val FETCH_ALLERGY_HISTORY = "MedicalHistory/GetPatientAllergyHistory"

        const val ADD_ATTACHMENT_REPORT = "MedicalHistory/AddPatientMedicalReport"
        const val DELETE_ATTACHMENT_REPORT = "MedicalHistory/DeletePatientMedicalReport"
        const val FETCH_ATTACHMENT_REPORT = "MedicalHistory/GetPatientMedicalReport"

        const val FETCH_BLOG = "Blog/GetBlogList"
        const val FETCH_BLOG_DETAIL = "Blog/GetBlogDetail"

        const val FETCH_APP_INFO="Content/GetAppSettings"
        const val FETCH_SPECIALITY = "Content/GetSpeciality"

        const val FETCH_PATIENT_INFO = "Patient/GetPatientDashboardInfo"

        const val FETCH_TOP_DOCTOR = "Doctor/GetTopDoctors"
        const val FETCH_DOCTOR = "Doctor/GetAllDoctorList"
        const val FETCH_DOCTOR_DETAIL= "Doctor/GetDoctorDescription"


        const val FETCH_DOCTOR_SCHEDULE="Schedule/GetAvailableDatesForPatient"
        const val FETCH_DOCTOR_TIME_SLOTS="Schedule/GetTimeSlotsForPatient"
        const val APPOINTMENT_REQUEST="Appointment/BookAppointment"
        const val APPOINTMENT_PROCESS="Appointment/UpdateAppointmentStatus"

        const val UPCOMING_APPOINTMENT="Appointment/GetPatientUpcomingAppointments"
        const val PAST_APPOINTMENT="Appointment/GetPatientPastAppointments"

        const val GRAB_VIDEO_TOKEN="Media/GetVideoCallToken"

        const val FETCH_DOC_RATING="Rating/GetUserRating"
        const val FETCH_DOC_REVIEW="Rating/GetUserReviews"
        const val ADD_DOC_RATING_REVIEW="Rating/AddUpdateUserRating"
    }

}
