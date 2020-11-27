package com.app.patlivecare.signin.view
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.patlivecare.LiveCareApplication
import com.app.patlivecare.extra.MinorActivity
import com.app.patlivecare.R
import com.app.patlivecare.annotation.FragmentType
import com.app.patlivecare.annotation.SignInType
import com.app.patlivecare.annotation.Status.FAILURE
import com.app.patlivecare.annotation.Status.SUCCESS
import com.app.patlivecare.base.BaseFragment
import com.app.patlivecare.base.BaseTextWatcher
import com.app.patlivecare.helper.SharedPrefHelper
import com.app.patlivecare.helper.Validator
import com.app.patlivecare.home.view.HomeActivity
import com.app.patlivecare.base.isUserInteractionEnabled
import com.app.patlivecare.network.WebHeader
import com.app.patlivecare.signin.model.SocialSignInRequest
import com.app.patlivecare.signin.viewmodel.SignInViewModel
import com.app.patlivecare.signup.model.SignUpResponse
import com.app.patlivecare.signup.view.AuthenticationActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SignInFragment : BaseFragment() {
    private var callbackManager: CallbackManager?=null
    private lateinit var viewModel: SignInViewModel
    private var sharedPrefs: SharedPrefHelper? =null
    private val DEVICE_TYPE = "android" // ios
    @SignInType private val signInType=SignInType.GENERAL
    private var googleSignInClient: GoogleSignInClient? = null

    companion object {
        fun newInstance() = SignInFragment()
    }

    override fun getRootView(): View {
       return ll_root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = (context.applicationContext as LiveCareApplication).getSharedPrefInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val headerMap = HashMap<String, String>()
        headerMap[WebHeader.KEY_CONTENT_TYPE] = WebHeader.VAL_CONTENT_TYPE
      //  headerMap[WebHeader.KEY_ACCEPT] = WebHeader.VAL_ACCEPT
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        viewModel.headerMap=headerMap
        viewModel.signInRequest.deviceType =DEVICE_TYPE
        viewModel.signInRequest.deviceToken ="12345" //firebase token
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initTextChangedListener()
        initObserver()

    }

    private fun initTextChangedListener() {
        edt_email?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {
                if (s.toString().isEmpty()) return

                if (Validator.isEmailValid(s.toString())) {
                    edt_email?.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick_green,
                        0
                    );
                } else {
                    edt_email?.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick_red,
                        0
                    );
                }
            }
        })
        edt_password?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(start: Int, before: Int, count: Int, s: CharSequence?) {

                if (s!!.isNotEmpty()) {
                    edt_password?.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick_green,
                        0
                    );
                } else {
                    edt_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        })

    }

    private fun initListener() {
        tv_forgot_password?.setOnClickListener {
            val intent= Intent(activity, MinorActivity::class.java)
            intent.putExtra("fragment_type", FragmentType.FORGOT_PASSWORD_FRAGMENT)
            startActivity(intent)

        }

        btn_sign_in?.setOnClickListener {

            attemptSignIn()
        }

        tv_sign_up?.setOnClickListener {
            (activity as AuthenticationActivity).showSignUpFragment()
        }

        edt_email?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_NEXT) {
                edt_password?.requestFocus()
            }
            true
        }
        edt_password?.setOnEditorActionListener { v, actionId, event ->
            if (actionId== EditorInfo.IME_ACTION_GO) {
                attemptSignIn()
            }
            true
        }


        ll_google_login?.setOnClickListener {
            viewModel.loadGoogle.value=true
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            val signInIntent: Intent? = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, 210)
        }

        ll_facebook_login?.setOnClickListener {
             callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(
                this, listOf(
                    "email",
                    "public_profile"
                )
            )
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        Log.e("result :", loginResult?.accessToken?.token.toString())
                        val request: GraphRequest =
                            GraphRequest.newMeRequest(loginResult?.accessToken) { dataObj, response ->
                                val email: String = dataObj.getString("email")
                                val id: String = dataObj.getString("id")
                                val fullName: String = dataObj.getString("name")
                                val fullNameList= fullName.split(" ")
                                val lastName=fullNameList.get(fullNameList.size-1)
                                val firstName=fullName.replace(lastName,"")
                                val signInRequest=SocialSignInRequest()
                                signInRequest.email=email
                                signInRequest.deviceToken="12345" //firebase token
                                signInRequest.deviceType=DEVICE_TYPE
                                signInRequest.firstName=firstName
                                signInRequest.lastName=lastName
                                signInRequest.loginType=SignInType.FACEBOOK
                                signInRequest.socialId=id
                                Log.e("result :", signInRequest.toString())
                                LoginManager.getInstance().logOut()
                                viewModel.attemptSocialSignIn(signInRequest,SignInType.FACEBOOK)
                            }
                        val parameters = Bundle()
                        parameters.putString("fields","id,name,email")
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        Log.e("onCancel : ", "onCancel")
                    }

                    override fun onError(exception: FacebookException) {
                        Log.e("onError :", exception?.toString())
                    }
                })
        }

    }

    @ExperimentalCoroutinesApi
    private fun attemptSignIn() {
        if(isFormValid()){
            viewModel.attemptSignIn()
        }

    }


    private fun isFormValid(): Boolean {
        var isEmail =true
        var isPassword =true
        val usrEmail = edt_email?.text.toString()
        val usrPassword = edt_password.text.toString()
        viewModel.signInRequest.email =usrEmail
        viewModel.signInRequest.password =usrPassword

        if (TextUtils.isEmpty(usrEmail)) {
            edt_email?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isEmail=false
        }
        if (usrEmail.contains(" ")) {
            edt_email?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isEmail=false
        }
        if (!Validator.isEmailValid(usrEmail)) {
            edt_email?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isPassword=false
        }
        if (usrPassword.isEmpty()) {
            edt_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isPassword=false
        }
        if (usrPassword.contains(" ")) {
            edt_password?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_red, 0)
            isPassword=false
        }
        return isEmail && isPassword
    }

    private fun initObserver() {
        viewModel.loadGeneral.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    pbar_general?.show()
                    btn_sign_in?.visibility = View.INVISIBLE
                } else {
                    pbar_general?.hide()
                    btn_sign_in?.visibility = View.VISIBLE
                }
            })



        viewModel.isViewEnable.observe(viewLifecycleOwner,
            Observer {
                if (it) view?.isUserInteractionEnabled(true)
                else view?.isUserInteractionEnabled(false)
            }
        )

        viewModel.resultantSignIn.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                SUCCESS -> handleSigInResult(it.data?.data)
                FAILURE -> showSnackBar(it.errorMsg.toString())
            }
        })

        viewModel.loadGoogle.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    pbar_google?.show()
                    ll_google_login?.visibility = View.INVISIBLE
                } else {
                    pbar_google?.hide()
                    ll_google_login?.visibility = View.VISIBLE
                }
            })
        viewModel.loadFacebook.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    pbar_facebook?.show()
                    ll_facebook_login?.visibility = View.INVISIBLE
                } else {
                    pbar_facebook?.hide()
                    ll_facebook_login?.visibility = View.VISIBLE
                }
            })

    }

    private fun handleSigInResult(data: SignUpResponse.Data?) {
        val address = data?.city.plus("|").plus(data?.state).plus("|").plus(data?.country)
        // comment
        sharedPrefs?.builder()?.write(SharedPrefHelper.KEY_FIRST_NAME, data!!.firstName)
            ?.write(SharedPrefHelper.KEY_LAST_NAME, data.lastName)
            ?.write(SharedPrefHelper.KEY_GENDER, data.gender)
            ?.write(SharedPrefHelper.KEY_EMAIL, data.email)
            ?.write(SharedPrefHelper.KEY_PHONE, data.dialCode + "|" + data.phoneNumber)
            ?.write(SharedPrefHelper.KEY_PROFILE_PIC, data.profilePic)
            ?.write(SharedPrefHelper.KEY_EMAIL_NOTIFICATION, data.emailNotificationStatus)
            ?.write(SharedPrefHelper.KEY_SMS_NOTIFICATION, data.smsNotificationStatus)
            ?.write(SharedPrefHelper.KEY_ACCESS_TOKEN, data.accessToken)
            ?.write(SharedPrefHelper.KEY_ADDRESS, address)
            ?.write(SharedPrefHelper.KEY_IS_SIGN_IN, true)
            ?.write(SharedPrefHelper.KEY_PROFILE_PERCENT, data.percentageProfileComplete)
            ?.write(SharedPrefHelper.KEY_IS_SOCIAL_SIGN_IN, data.isSocialUser)
            ?.build()

        val intent= Intent(activity, HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.loadGoogle.value=false
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 210) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
      try{
        val acc = completedTask.getResult(ApiException::class.java)
          val fullNameList=acc?.displayName?.split(" ")
          val lastName=fullNameList?.get(fullNameList.size - 1)?:""
          val firstName=acc?.displayName?.replace(lastName, "")?:""
            val signInRequest = SocialSignInRequest()
            signInRequest.email=acc?.email.toString()
            signInRequest.deviceToken="12345" //firebase token
            signInRequest.deviceType=DEVICE_TYPE
            signInRequest.firstName=firstName
            signInRequest.lastName=lastName
            signInRequest.loginType=SignInType.GOOGLE
            signInRequest.socialId=acc?.id.toString()
           // Log.e("Result : ",signInRequest.toString())
           googleSignInClient?.signOut()
           viewModel.attemptSocialSignIn(signInRequest, SignInType.GOOGLE)
    } catch (e: ApiException) {
        showSnackBar(getString(R.string.alert_please_try_again))
    }

    }
// LoginManager.getInstance().logOut();


}
