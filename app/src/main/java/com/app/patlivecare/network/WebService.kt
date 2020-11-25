package com.app.patlivecare.network

import com.app.patlivecare.extra.GeneralResponse
import com.app.patlivecare.appointment.model.DoctorScheduleResponse
import com.app.patlivecare.appointment.model.DoctorTimeSlotResponse
import com.app.patlivecare.blog.model.BlogDetailResponse
import com.app.patlivecare.blog.model.BlogResponse
import com.app.patlivecare.consult.model.PastAppointmentResponse
import com.app.patlivecare.consult.model.UpcomingAppointmentResponse
import com.app.patlivecare.doctor.model.DoctorDetailResponse
import com.app.patlivecare.rating.model.DoctorReviewResponse
import com.app.patlivecare.doctor.model.SpecialityResponse
import com.app.patlivecare.doctor.model.DoctorResponse
import com.app.patlivecare.home.model.PatientInfoResponse
import com.app.patlivecare.medicalrecord.model.*
import com.app.patlivecare.miscellaneous.model.AppInfoResponse
import com.app.patlivecare.miscellaneous.model.ContactUsRequest
import com.app.patlivecare.password.model.ForgotPasswordResponse
import com.app.patlivecare.password.model.SendOtpResponse
import com.app.patlivecare.profile.model.*
import com.app.patlivecare.rating.model.DoctorRatingResponse
import com.app.patlivecare.signin.model.SignInRequest
import com.app.patlivecare.signin.model.SocialSignInRequest
import com.app.patlivecare.signup.model.SignUpRequest
import com.app.patlivecare.signup.model.SignUpResponse
import com.app.patlivecare.videocall.model.VideoTokenResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface WebService {

    @Headers("content-type:application/json")
    @POST(WebUrl.SIGN_IN)
    suspend fun attemptSignIn(@HeaderMap token:Map<String, String>, @Body input: SignInRequest): SignUpResponse

    @Headers("content-type:application/json")
    @POST(WebUrl.SIGN_UP)
    suspend fun attemptSignUp(@Body input: SignUpRequest): SignUpResponse

    @POST(WebUrl.SIGN_OUT)
    suspend fun attemptSignOut(@HeaderMap token: Map<String, String>): GeneralResponse

    @Headers("content-type:application/json")
    @POST(WebUrl.SOCIAL_SIGN_IN)
    suspend fun attemptSocialSignIn(@HeaderMap token:Map<String, String>, @Body input: SocialSignInRequest): SignUpResponse

    // profile
    @GET(WebUrl.BASIC_PROFILE_INFO)
    suspend fun fetchBasicProfileInfo(@HeaderMap token: Map<String, String>):BasicInfoResponse

    // profile
    @GET(WebUrl.ADDITIONAL_PROFILE_INFO)
    fun fetchAdditionalProfileInfo(@HeaderMap token: Map<String, String>): Call<AdditionalInfoResponse>?

    // profile
    @Multipart
    @POST(WebUrl.ALTER_PROFILE_PIC)
    suspend fun alterProfilePic(@HeaderMap token: Map<String, String>, @Part imgFile: MultipartBody.Part): AlterProfilePicResponse // imgFile

    // profile
    @POST(WebUrl.UPDATE_BASIC_PROFILE_INFO)
    suspend fun updateBasicProfileInfo(@HeaderMap token: Map<String, String>,@Body inn: BasicProfileRequest): GeneralResponse

    // profile
    @POST(WebUrl.UPDATE_ADDITIONAL_PROFILE_INFO)
    fun updateAdditionalProfileInfo(@HeaderMap token: Map<String, String>,@Body inn: AdditionalProfileInfo): Call<GeneralResponse>?

    @Headers("content-type:application/json")
    @GET(WebUrl.FETCH_COUNTRY)
    suspend fun fetchCountries(): CountryInfoResponse

    @Headers("content-type:application/json")
    @GET(WebUrl.FETCH_STATE)
    suspend fun fetchStates(@Query("CountryId")  countryId :Int): StateInfoResponse


    @Headers("content-type:application/json")
    @POST(WebUrl.SEND_EMAIL_OTP)
    suspend fun attemptSendOtpAtEmail(@Body inn: JsonObject): SendOtpResponse


    @Headers("content-type:application/json")
    @POST(WebUrl.VERIFY_EMAIL)
    suspend fun attemptVerifyEmail(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse


    @POST(WebUrl.SEND_PHONE_OTP)
    suspend fun attemptSendOtpAtPhone(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse

    @POST(WebUrl.VERIFY_PHONE)
    suspend fun attemptVerifyPhone(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse

    @POST(WebUrl.CHANGE_PASSWORD)
    suspend fun attemptChangePassword(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse

    @Headers("content-type:application/json")
    @POST(WebUrl.FORGOT_PASSWORD)
    suspend fun attemptForgotPassword(@Body inn: JsonObject): ForgotPasswordResponse

    @Headers("content-type:application/json")
    @POST(WebUrl.RESET_PASSWORD)
    suspend fun attemptResetPassword(@Body inn: JsonObject): GeneralResponse


    @POST(WebUrl.EN_DIS_NOTIFICATION)
    suspend fun alterNotificationService(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse

    @POST(WebUrl.ADD_PAST_MEDICAL_HISTORY)
    suspend fun addPastMedicalHistory(@HeaderMap token: Map<String, String>,@Body inn: PastMedicalHistoryInfo): GeneralResponse
    @GET(WebUrl.FETCH_PAST_MEDICAL_HISTORY)
    suspend fun fetchPastMedicalHistory(@HeaderMap token: Map<String, String>): PastMedicalHistoryResponse
    @DELETE(WebUrl.DELETE_PAST_MEDICAL_HISTORY)
    suspend fun deletePastMedicalHistory(@HeaderMap token: Map<String, String>,@Query("MedicalHistoryId") id:Int): GeneralResponse

    @POST(WebUrl.ADD_SURGICAL_HISTORY)
    suspend fun addSurgicalHistory(@HeaderMap token: Map<String, String>,@Body inn: SurgicalHistoryInfo): GeneralResponse
    @GET(WebUrl.FETCH_SURGICAL_HISTORY)
    suspend fun fetchSurgicalHistory(@HeaderMap token: Map<String, String>): SurgicalHistoryResponse
    @DELETE(WebUrl.DELETE_SURGICAL_HISTORY)
    suspend fun deleteSurgicalHistory(@HeaderMap token: Map<String, String>,@Query("surgicalHistoryId") id:Int): GeneralResponse

    @POST(WebUrl.ADD_FAMILY_HISTORY)
    suspend fun addFamilyHistory(@HeaderMap token: Map<String, String>,@Body inn: FamilyHistoryInfo): GeneralResponse
    @GET(WebUrl.FETCH_FAMILY_HISTORY)
    suspend fun fetchFamilyHistory(@HeaderMap token: Map<String, String>): FamilyHistoryResponse
    @DELETE(WebUrl.DELETE_FAMILY_HISTORY)
    suspend fun deleteFamilyHistory(@HeaderMap token: Map<String, String>,@Query("familyHistoryId") id:Int): GeneralResponse


    @POST(WebUrl.ADD_ALLERGY_HISTORY)
    suspend fun addAllergyHistory(@HeaderMap token: Map<String, String>,@Body inn: AllergyHistoryInfo): GeneralResponse
    @GET(WebUrl.FETCH_ALLERGY_HISTORY)
    suspend fun fetchAllergyHistory(@HeaderMap token: Map<String, String>): AllergyHistoryResponse
    @DELETE(WebUrl.DELETE_ALLERGY_HISTORY)
    suspend fun deleteAllergyHistory(@HeaderMap token: Map<String, String>,@Query("allergyHistoryId") id:Int): GeneralResponse

    @Multipart
    @POST(WebUrl.ADD_ATTACHMENT_REPORT)
    suspend fun addAttachmentHistory(@HeaderMap token: Map<String, String>,
                                     @Part("ReportName") reportName: RequestBody,
                                     @Part("Date") date: RequestBody,
                                     @Part("Description") desc: RequestBody,
                                     @Part documentFile: MultipartBody.Part): GeneralResponse // MedicalDocument

    @GET(WebUrl.FETCH_ATTACHMENT_REPORT)
    suspend fun fetchAttachmentHistory(@HeaderMap token: Map<String, String>): AttachmentHistoryResponse
    @DELETE(WebUrl.DELETE_ATTACHMENT_REPORT)
    suspend fun deleteAttachmentHistory(@HeaderMap token: Map<String, String>,@Query("medicalReportId") id:Int): GeneralResponse



    @GET(WebUrl.FETCH_BLOG)
    fun fetchBlogs(@HeaderMap token: Map<String, String>,@Query("pageNumber") pageNo:Long,@Query("pageSize") pageSize:Int=10): Call<BlogResponse>


    @GET(WebUrl.FETCH_BLOG_DETAIL)
    suspend fun fetchBlogDetail(@HeaderMap token: Map<String, String>,@Query("blogId") id:String): BlogDetailResponse


    @POST(WebUrl.CONTACT_US)
    suspend fun attemptContactUs(@HeaderMap token: Map<String, String>,@Body inn: ContactUsRequest): GeneralResponse

    @GET(WebUrl.FETCH_APP_INFO)
    suspend fun fetchAppInfo(): AppInfoResponse

    @GET(WebUrl.FETCH_SPECIALITY)
    suspend fun fetchSpeciality(@HeaderMap token: Map<String, String>): SpecialityResponse

    @GET(WebUrl.FETCH_PATIENT_INFO)
    suspend fun fetchPatientInfo(@HeaderMap token: Map<String, String>): PatientInfoResponse

    @GET(WebUrl.FETCH_TOP_DOCTOR)
    suspend fun fetchTopDoctor(@HeaderMap token: Map<String, String>,@Query("pageNumber") pageNo:Int,@Query("pageSize") pageSize:Int=10): DoctorResponse

    @GET(WebUrl.FETCH_TOP_DOCTOR)
    fun fetchTopDoctorWithPage(@HeaderMap token: Map<String, String>,@Query("pageNumber") pageNo:Long,@Query("pageSize") pageSize:Int=10): Call<DoctorResponse>

    @GET(WebUrl.FETCH_DOCTOR)
    fun fetchDoctor(@HeaderMap token: Map<String, String>,@Query("pageNumber") pageNo:Long,
                    @Query("pageSize") pageSize:Int=10,
                    @Query("searchQuery") searchQuery:String="",
                    @Query("filterBy") specialityId:String=""): Call<DoctorResponse>

    @GET(WebUrl.FETCH_DOCTOR)
    suspend fun fetchSearchDoctor(@HeaderMap token: Map<String, String>,@Query("pageNumber") pageNo:Long,
                    @Query("pageSize") pageSize:Int=10,
                    @Query("searchQuery") searchQuery:String="",
                    @Query("filterBy") specialityId:String=""): DoctorResponse

    @GET(WebUrl.FETCH_DOCTOR_DETAIL)
    suspend fun fetchDoctorDetails(@HeaderMap token: Map<String, String>,@Query("DoctorId") doctorId:String): DoctorDetailResponse

    @GET(WebUrl.FETCH_DOCTOR_SCHEDULE)
    suspend fun fetchDoctorSchedule(@HeaderMap token: Map<String, String>,@Query("DoctorId") doctorId:String,@Query("Date") selectedDate:String): DoctorScheduleResponse

    @GET(WebUrl.FETCH_DOCTOR_TIME_SLOTS)
    suspend fun fetchDoctorTimeSlot(@HeaderMap token: Map<String, String>,@Query("DoctorId") doctorId:String, @Query("selectedDate") selectedDate:String): DoctorTimeSlotResponse

    @POST(WebUrl.APPOINTMENT_REQUEST)
    suspend fun attemptAppointmentRequest(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse

    @GET(WebUrl.UPCOMING_APPOINTMENT)
    suspend fun fetchUpcomingAppointment(@HeaderMap token: Map<String, String>,@Query("appointmentStatus") appointmentStatus:Int): UpcomingAppointmentResponse

    @GET(WebUrl.PAST_APPOINTMENT)
    fun fetchPastAppointment(@HeaderMap token: Map<String, String>,@Query("pageNumber") pageNo:Int, @Query("pageSize") pageSize:Int=10): Call<PastAppointmentResponse>

    @POST(WebUrl.APPOINTMENT_PROCESS)
    suspend fun attemptAppointmentProcess(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse

    @GET(WebUrl.GRAB_VIDEO_TOKEN)
    fun fetchVideoToken(@HeaderMap token: Map<String, String>,@Query("AppointmentId") appointmentId:String): VideoTokenResponse


    @GET(WebUrl.FETCH_DOC_RATING)
    suspend fun fetchDoctorAppointmentRating(@HeaderMap token: Map<String, String>,@Query("appointmentId") appointmentId:String): DoctorRatingResponse

    @GET(WebUrl.FETCH_DOC_REVIEW)
    suspend fun fetchDoctorReview(@HeaderMap token: Map<String, String>,@Query("DoctorId") doctorId:String): DoctorReviewResponse

    @POST(WebUrl.ADD_DOC_RATING_REVIEW)
    suspend fun addDoctorRatingAndReview(@HeaderMap token: Map<String, String>,@Body inn: JsonObject): GeneralResponse

}