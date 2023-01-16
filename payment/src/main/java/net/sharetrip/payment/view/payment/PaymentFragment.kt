package net.sharetrip.payment.view.payment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.payment.model.BookingResponse
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.payment.databinding.FragmentPaymentBinding
import net.sharetrip.payment.model.PaymentUrl
import net.sharetrip.payment.R
import net.sharetrip.payment.convertPaymentUrlList
import net.sharetrip.payment.convertToUrlModel
import net.sharetrip.shared.utils.*

class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {
    private var bookingResponseLinks = ""
    private var paymentURL = ""
    private var paymentModel: BookingResponse? = null
    private var webViewBundle: Bundle? = null
    private var serviceType = ""
    private var serviceSuccessUrl = ""
    private var serviceFailureUrl = ""
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    private val paymentSuccessUrl = "https://sharetrip.net/profile?route=bookings"
    private val paymentFailUrl = "https://sharetrip.net/?type=failed"
    private val paymentSuccessUrlStg = "https://stg.sharetrip.net/profile?route=bookings"
    private val paymentFailUrlStg = "https://stg.sharetrip.net/?type=failed"

    private val paymentEventManager =
        AnalyticsProvider.paymentEventManager(AnalyticsProvider.getFirebaseAnalytics())

    override fun layoutId(): Int = R.layout.fragment_payment

    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun initOnCreateView() {
        bookingResponseLinks = arguments?.getString(ARG_PAYMENT_URL).toString()
        serviceType = arguments?.getString(SERVICE_TYPE).toString()

        sharedPrefsHelper = SharedPrefsHelper(requireContext())

        val settings = bindingView.webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.setSupportZoom(true)
        settings.displayZoomControls = false

        bindingView.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                when (url) {
                    serviceSuccessUrl -> {
                        sentPaymentSuccessEvent()
                        showToast(PAYMENT_CONFIRM_MSG)
                        findNavController().navigateSafe(R.id.action_paymentMethodFragment_to_paymentSuccessFragment)
                        return true
                    }

                    paymentSuccessUrl, paymentSuccessUrlStg -> {
                        sentPaymentSuccessEvent()
                        showToast(PAYMENT_CONFIRM_MSG)
                        findNavController().navigateSafe(R.id.action_paymentMethodFragment_to_paymentSuccessFragment)
                        return true
                    }

                    serviceFailureUrl -> {
                        sentPaymentFailedEvent()

                        try {
                            findNavController().navigateSafe(R.id.action_paymentMethodFragment_to_paymentFailFragment)
                        }
                        catch (e:Exception){
                            e.printStackTrace()
                        }

                        return true
                    }

                    paymentFailUrl, paymentFailUrlStg -> {
                        sentPaymentFailedEvent()

                        try {
                            findNavController().navigateSafe(R.id.action_paymentMethodFragment_to_paymentFailFragment)
                        }
                        catch (e:Exception){
                            e.printStackTrace()
                        }

                        return true
                    }

                    else -> {
                        view.loadUrl(url)
                        return true
                    }
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                bindingView.webView.visibility = View.VISIBLE
                bindingView.layoutProgress.visibility = View.GONE
                bindingView.progressBar.isIndeterminate = false
                super.onPageFinished(view, url)
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                bindingView.layoutProgress.visibility = View.VISIBLE
                bindingView.progressBar.isIndeterminate = true
                super.onPageStarted(view, url, favicon)
            }
        }

        bindingView.webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                return true
            }
        }

        bindingView.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        bindingView.webView.settings.domStorageEnabled = true

        val jsonObject = BookingUrlParser.parseToJSON(bookingResponseLinks)

        if (jsonObject.length() == 0) {
            paymentURL = bookingResponseLinks
            getPaymentURL()
        } else {
            paymentModel = jsonObject.toString().convertToUrlModel()
            paymentURL = paymentModel?.paymentUrl.toString()
            serviceSuccessUrl = paymentModel?.successUrl.toString()
            serviceFailureUrl = paymentModel?.cancelUrl.toString()
        }

        if (isOnline()) {
            when (webViewBundle) {

                null -> bindingView.webView.loadUrl(paymentURL)

                else -> bindingView.webView.restoreState(webViewBundle!!)
            }
        } else {
            val summary =
                "<html><body><font color='red'>No Internet Connection</font></body></html>"
            bindingView.webView.loadData(summary, "text/html", null)
            showToast(NO_INTERNET_MSG)
        }

        bindingView.webView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
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
    }

    private fun isOnline(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }

    private fun sentPaymentSuccessEvent() {
        when (serviceType) {
            SERVICE_TYPE_FLIGHT -> paymentEventManager.paymentCompleteFlight()

            SERVICE_TYPE_HOLIDAY -> paymentEventManager.paymentCompleteHoliday()

            SERVICE_TYPE_HOTEL -> paymentEventManager.paymentCompleteHotel()

            SERVICE_TYPE_VISA -> paymentEventManager.paymentCompleteVisa()
        }
    }

    private fun sentPaymentFailedEvent() {
        when (serviceType) {
            SERVICE_TYPE_FLIGHT -> paymentEventManager.paymentFailedFlight()

            SERVICE_TYPE_HOLIDAY -> paymentEventManager.paymentFailedHoliday()

            SERVICE_TYPE_HOTEL -> paymentEventManager.paymentFailedHotel()

            SERVICE_TYPE_VISA -> paymentEventManager.paymentFailedVisa()
        }
    }

    private fun getPaymentURL() {
        val urls = sharedPrefsHelper[PrefKey.PAYMENT_URLS, ""].convertPaymentUrlList()

        if (!urls.isNullOrEmpty()) {
            when (serviceType) {
                SERVICE_TYPE_FLIGHT -> setServiceUrl(urls, FLIGHT_TYPE)

                SERVICE_TYPE_HOLIDAY -> setServiceUrl(urls, HOLIDAY_TYPE)

                SERVICE_TYPE_HOTEL -> setServiceUrl(urls, HOTEL_TYPE)

                SERVICE_TYPE_VISA -> setServiceUrl(urls, VISA_TYPE)
            }
        }
    }

    private fun setServiceUrl(urls: List<PaymentUrl>, type: String) {
        for (serviceUrl in urls) {
            if (serviceUrl.service_type == type && serviceUrl.status == PAYMENT_SUCCESS_STATUS)
                serviceSuccessUrl = serviceUrl.url

            if (serviceUrl.service_type == type && serviceUrl.status == PAYMENT_FAILURE_STATUS)
                serviceFailureUrl = serviceUrl.url
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        bindingView.webView.onPause()

        webViewBundle = Bundle()
        bindingView.webView.saveState(webViewBundle!!)
    }

    override fun onResume() {
        super.onResume()
        bindingView.webView.onResume()
    }

    private companion object {
        const val PAYMENT_SUCCESS_STATUS = "success"
        const val PAYMENT_FAILURE_STATUS = "failed"
        const val FLIGHT_TYPE = "Flight"
        const val HOLIDAY_TYPE = "Package"
        const val HOTEL_TYPE = "Hotel"
        const val VISA_TYPE = "Visa"
        const val NO_INTERNET_MSG = "No Internet Connection."
        const val PAYMENT_CONFIRM_MSG = "We will confirm payment soon"
    }
}
