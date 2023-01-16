package net.sharetrip.signup.view.login.social

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.utils.ShareTripAppConstants
import com.sharetrip.base.utils.ShareTripAppConstants.APPLE_TOKEN_KEY
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.signup.R
import net.sharetrip.signup.databinding.FragmentSocialLoginBinding
import net.sharetrip.signup.network.SignUpDataManager
import net.sharetrip.signup.view.RegistrationActivity

class SocialLoginFragment : BaseFragment<FragmentSocialLoginBinding>() {

    private lateinit var appleAccessToken: String
    private lateinit var appleDialog: Dialog
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var gso: GoogleSignInOptions

    private val viewModel by lazy {
        SocialLoginVMFactory(
            SignUpDataManager.signupApiService,
            SignUpDataManager.signUpSharedPref(requireContext())
        ).create(SocialLoginViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.fragment_social_login

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("346164418203-h0aa85kn3dltnoq44lvljttpdlkcdm10.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onCancel() {}

            override fun onError(error: FacebookException) {}

            override fun onSuccess(result: LoginResult?) {
                result?.accessToken?.token?.let { viewModel.loginWithFacebook(it) }
            }
        })

        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                } else
                    Toast.makeText(requireContext(), "Something went wrong.", Toast.LENGTH_SHORT)
                        .show()
            }

        bindingView.facebookLoginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this,callbackManager, listOf(EMAIL, PUBLIC_PROFILE))
        }

        bindingView.btnGoogleLogin.setOnClickListener {
            viewModel.requestCode = APP_REQUEST_CODE_GOOGLE
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult.launch(signInIntent)
        }

        bindingView.btnAppleLogin.setOnClickListener {
            setupAppleWebViewDialog(ShareTripAppConstants.getAppleAuthUrl())
        }


        viewModel.goToEmailLogin.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_socialLoginFragment_to_emailLoginFragment)
        })

        viewModel.goToSignup.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_socialLoginFragment_to_signupFragment)
        })

        viewModel.skipClicked.observe(viewLifecycleOwner, EventObserver {
            (activity as RegistrationActivity).finish()
        })

        viewModel.errorText.observe(viewLifecycleOwner) { s: String? ->
            Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show()
        }

        viewModel.setActivityResult.observe(viewLifecycleOwner) {
            if (it) {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupAppleWebViewDialog(url: String) {
        appleDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        appleDialog.setContentView(R.layout.fragment_apple_sign_in)
        val layout = appleDialog.findViewById<LinearLayout>(R.id.layoutProgress)
        val progressBar = appleDialog.findViewById<ProgressBar>(R.id.progressBar)
        val webView = appleDialog.findViewById<WebView>(R.id.webView)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.setSupportZoom(true)
        settings.displayZoomControls = false

        webView.settings.userAgentString = "Android_Tbbd"
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.domStorageEnabled = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("appleToken=", false)) {
                    handleURL(url)
                    appleDialog.dismiss()

                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                webView.visibility = View.VISIBLE
                layout.visibility = View.GONE
                progressBar.isIndeterminate = false
                super.onPageFinished(view, url)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                return true
            }
        }

        webView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val webView = v as WebView
                when (keyCode) {
                    KeyEvent.KEYCODE_BACK -> if (webView.canGoBack()) {
                        webView.goBack()
                        return@OnKeyListener true
                    }
                }
            }
            false
        })

        if (isOnline()) {
            webView.loadUrl(url)
        } else {
            val summary =
                "<html><body><font color='red'>No Internet Connection</font></body></html>"
            webView.loadData(summary, "text/html", null)
            Toast.makeText(requireContext(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
        }

        appleDialog.show()
    }

    private fun isOnline(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    private fun handleURL(url: String) {
        val uri = Uri.parse(url)
        appleAccessToken = uri.getQueryParameter(APPLE_TOKEN_KEY)!!
        viewModel.loginWithApple(appleAccessToken)
    }

    override fun onStart() {
        super.onStart()
        val alreadyLoggedAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (alreadyLoggedAccount != null) {
            googleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(
                    requireContext(),
                    "Google Sign out successfully",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.loginWithGoogle(account)
        } catch (e: ApiException) {
        }
    }

    companion object {
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"
        var APP_REQUEST_CODE_GOOGLE = 100
    }
}