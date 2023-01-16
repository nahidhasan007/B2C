package net.sharetrip.hotel.booking.view.hoteldiscount

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.GatewayService
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.utils.ShareTripAppConstants
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.*
import net.sharetrip.hotel.booking.model.coupon.*
import net.sharetrip.hotel.booking.model.localdatasource.UIMessageData
import net.sharetrip.hotel.booking.model.payment.COUPON
import net.sharetrip.hotel.booking.model.payment.EARN
import net.sharetrip.hotel.booking.model.payment.PaymentMethod
import net.sharetrip.hotel.booking.model.payment.REDEEM
import net.sharetrip.hotel.booking.view.travellerlist.TravellerListFragment.Companion.ARG_TRAVELLER_LIST_BOOKING_SUMMARY_STRING
import net.sharetrip.hotel.booking.view.travellerlist.TravellerListFragment.Companion.ARG_TRAVELLER_LIST_DISCOUNT_MODEL
import net.sharetrip.hotel.booking.view.travellerlist.TravellerListFragment.Companion.ARG_TRAVELLER_LIST_HOTEL_BOOKING_PARAMS_MODEL
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.hotel.utils.MsgUtils.COUPON_ADDED_SUCCESSFULLY
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.model.user.User
import net.sharetrip.shared.utils.UtilText
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.shared.utils.isPhoneNumberValid
import net.sharetrip.shared.utils.twoDigitDecimal
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class HotelDiscountViewModel(
    private val hotelBookingParams: HotelBookingParams,
    val summary: RoomBookingSummary,
    promotionalCoupons: ListOfCoupon,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel(), GuestLoginListener {
    private var token = ""
    private val allPaymentMethod: MutableLiveData<List<PaymentMethod>> = MutableLiveData()
    private val convenienceVatFlag = false
    private var userTripCoin: Int = 0
    private val hotelEventManager =
        AnalyticsProvider.hotelEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private var isRedeemCodeActivated: Boolean = false
    private val discountModel = DiscountModel()
    private var isFirstTime = true
    private lateinit var timer: CountDownTimer
    private var gpCoupon = ""
    private var couponDiscount = 0.0
    private var isCouponApplied = ObservableBoolean(false)
    private var isGPCouponApplied = false
    private var preVCouponCode = ""
    private var verifiedOTP = ""
    val redeemCoin = ObservableInt()
    var progressForTripCoin: Int = 0
    val wannaRedeem = ObservableBoolean()
    var bankDiscountInfo: String = ""
    val isCouponShow = ObservableBoolean(true)
    val isGPStarCouponShow = ObservableBoolean(false)
    val totalPrice = ObservableInt()
    val discountAmount = ObservableInt()
    val couponObserver = ObservableField<HotelCouponRequest>()
    val isCardSelected = ObservableBoolean(true)
    val isRedeemSelected = ObservableBoolean(false)
    val isCouponSelected = ObservableBoolean(false)
    val redeemChecked = MutableLiveData<Boolean>()
    val cardPaymentChecked = MutableLiveData<Boolean>()
    val availCoupon = MutableLiveData<Boolean>()
    val earnText = ObservableField<String>()
    val redeemText = ObservableField<String>()
    val couponText = ObservableField<String>()
    val isDismissDialog = MutableLiveData<Boolean>()
    var isLogin = ObservableBoolean()
    val isConvenienceVisible = ObservableBoolean(false)
    val totalConvenienceCharge = ObservableField(0)
    val vatCharge = ObservableField(0)
    val hideKeyboard = SingleLiveEvent<Any>()
    val observableUserTripCoin = SingleLiveEvent<String>()
    val navigateToTravellerList = SingleLiveEvent<Bundle>()
    val navigateToLogin = SingleLiveEvent<Boolean>()
    val couponList = MutableLiveData<List<PromotionalCoupon>>()
    var isExtraDiscountVisible = ObservableBoolean(false)
    var extraDiscountLabel = ObservableField<String>()
    val extraDiscountAmount = ObservableInt()
    var gpCouponTitle = ObservableField(UtilText.couponTitle)
    var gpCouponSubTitle = ObservableField(UtilText.couponSubTitle)
    val countdownTimerOrResendText = ObservableField<String>()
    val couponState = MutableLiveData(GPCouponInputState.MobileInputState.name)
    val inputObserver = ObservableField<String>()
    var gpStarNumber = ""
    var gpCouponInputHint = ObservableField(UtilText.enterPhone)
    val isGpStarCouponVerified = MutableLiveData<Boolean>()
    val isCouponAvailable = ObservableBoolean()

    val isShowDialog: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isLoginLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val maxTripCoin: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    init {
        token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        totalPrice.set(ceil(summary.totalPayableBeforeDiscount() - summary.totalDiscount()).toInt())
        discountAmount.set(ceil(summary.totalDiscount()).toInt())
        redeemCoin.set(0)
        wannaRedeem.set(false)
        couponObserver.set(HotelCouponRequest())

        val listType = object : TypeToken<List<RemoteDiscountModel>>() {}.type
        val remoteDiscountModel = Gson().fromJson<List<RemoteDiscountModel>>(
            sharedPrefsHelper[PrefKey.HOTEL_DISCOUNT_OPTIONS, PrefKey.DEFAULT_HOTEL_DISCOUNT_OPTION],
            listType
        )
        earnText.set(remoteDiscountModel.find { it.type == EARN }?.title)
        redeemText.set(remoteDiscountModel.find { it.type == REDEEM }?.title)
        couponText.set(remoteDiscountModel.find { it.type == COUPON }?.title)

        checkLoginInformation()
        fetchPaymentGateWay()
        setUserTripCoin()

        if (promotionalCoupons.couponList.isNotEmpty())
            onCouponChecked()

        couponList.value = promotionalCoupons.couponList
    }

    private fun setUserTripCoin() {
        try {
            userTripCoin =
                sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""].filter { it in '0'..'9' }.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        maxTripCoin.value = if (userTripCoin > summary.earnTripCoin) {
            summary.earnTripCoin
        } else {
            userTripCoin
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        when (operationTag) {
            HotelDiscountApiCallingKey.PaymentGateWay.name -> {
                val response = (data.body as RestResponse<*>).response as List<PaymentMethod>
                allPaymentMethod.value = response
                val paymentMethodWithCoupon: MutableList<PaymentMethod> = ArrayList()
                paymentMethodWithCoupon.addAll(response.filter { it.isCouponAvailable })

                if (paymentMethodWithCoupon.isEmpty()) {
                    isCouponShow.set(false)
                }

                calculatePayableAmount()
            }

            HotelDiscountApiCallingKey.UserProfile.name -> {
                val userRestResponse = (data.body as RestResponse<*>).response as User

                val tripCoin = NumberFormat.getNumberInstance(Locale.US)
                    .format(userRestResponse.totalPoints.toLong())
                observableUserTripCoin.value = tripCoin
                sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, tripCoin)
                setUserTripCoin()
            }

            HotelDiscountApiCallingKey.CouponRequest.name -> {
                val couponResponse = (data.body as RestResponse<*>).response as CouponResponse
                discountModel.type = couponResponse.discountType
                discountModel.gateway = couponResponse.gateway

                isGPStarCouponShow.set(
                    couponResponse.mobileVerificationRequired?.lowercase().equals("yes")
                )

                couponObserver.get()?.coupon?.let {
                    updateCouponDetailsUI(it, couponResponse)
                }

                if (isGPStarCouponShow.get()) {
                    couponObserver.get()?.coupon?.let {
                        gpCoupon = it
                    }

                    isCouponApplied.set(false)
                    isGpStarCouponVerified.postValue(true)
                    showMessage(UtilText.gpNumberVerify)
                }
            }

            HotelDiscountApiCallingKey.GpLoyaltyCheck.name -> {
                dataLoading.set(false)
                val response = (data.body as RestResponse<*>).response as GPLoyaltyCheckResponse
                if (response.success == true) {
                    hotelBookingParams.verifiedMobileNumber = gpStarNumber
                    showMessage(UtilText.enterOtp)
                    inputObserver.set("")
                    gpCouponInputHint.set(UtilText.enterOtp)
                    couponState.value = GPCouponInputState.OTPInputState.name

                    try {
                        counter((response.otpExpirationInMin.toDouble() * 60000).toLong())
                    } catch (e: Exception) {
                        counter()
                    }

                } else {
                    showMessage((data.body as RestResponse<*>).message)
                }
            }
            HotelDiscountApiCallingKey.OTPVerify.name -> {
                hotelBookingParams.otp = verifiedOTP
                timer.cancel()
                discountModel.coupon = gpCoupon
                countdownTimerOrResendText.set("")
                couponState.value = GPCouponInputState.MobileInputState.name
                gpCouponInputHint.set(UtilText.enterPhone)
                isGPStarCouponShow.set(false)
                showMessage((data.body as RestResponse<*>).message)
                dataLoading.set(false)
                isCouponApplied.set(true)
                showMessage(COUPON_ADDED_SUCCESSFULLY)
                isGPCouponApplied = true
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    fun checkLoginInformation() {
        token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        isLogin.set(sharedPrefsHelper[PrefKey.IS_LOGIN, false])
        isLoginLiveData.value = isLogin.get()
    }

    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {}

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!isFirstTime) {
                redeemCoin.set(progress)
                extraDiscountAmount.set(progress)
                progressForTripCoin = progress
                discountModel.discountAmount = redeemCoin.get().toDouble()
                calculatePayableAmount()
                observableUserTripCoin.value =
                    NumberFormat.getNumberInstance(Locale.US).format(userTripCoin - progress)
            }
            isFirstTime = false
        }
    }

    fun onClickBookNow() {
        if (isCouponSelected.get() && (discountModel.coupon.isEmpty() || !isCouponApplied.get())) {
            showMessage("Please apply a coupon or select another option")
        } else {
            if (sharedPrefsHelper[PrefKey.IS_LOGIN, false]) {
                val bookingParam = MoshiUtil.convertBookingParamToString(hotelBookingParams)
                val summary = MoshiUtil.convertBookingSummaryToString(summary)
                val bundle = Bundle()
                bundle.putString(ARG_TRAVELLER_LIST_HOTEL_BOOKING_PARAMS_MODEL, bookingParam)
                bundle.putString(ARG_TRAVELLER_LIST_BOOKING_SUMMARY_STRING, summary)

                when (discountModel.disCountMethod) {
                    "Coin", "Card" -> {
                        discountModel.coupon = ""
                        discountModel.couponWithDiscount = false
                    }
                }

                bundle.putParcelable(ARG_TRAVELLER_LIST_DISCOUNT_MODEL, discountModel)
                navigateToTravellerList.value = bundle
            } else {
                isShowDialog.value = false
            }
        }
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    fun onInformationIconClick(view: View) {
        val dialog = Dialog(view.context, R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_hotel_rate_info)
        val perDayLayout: LinearLayout = dialog.findViewById(R.id.perDayLayout)
        val totalPriceHints: AppCompatTextView = dialog.findViewById(R.id.totalPriceHints)
        val totalPrice: AppCompatTextView = dialog.findViewById(R.id.totalPrice)
        val avgPrice: AppCompatTextView = dialog.findViewById(R.id.avgPrice)
        val textClose: AppCompatTextView = dialog.findViewById(R.id.textClose)

        summary.dateList.forEach {
            val layoutView = LayoutInflater.from(perDayLayout.context)
                .inflate(R.layout.hotel_per_day_price_layout, null, true)
            val dayName: AppCompatTextView = layoutView.findViewById(R.id.dayName)
            val dayPrice: AppCompatTextView = layoutView.findViewById(R.id.dayPrice)
            dayName.text = summary.totalRoom.toString() + " X " + it
            dayPrice.text = summary.firstRoomPayablePerNight()
            perDayLayout.addView(layoutView)
        }
        totalPriceHints.text = "Total for ${summary.totalNight} Night(s)"
        totalPrice.text = "BDT " + summary.firstRoomPayable()
        avgPrice.text = "BDT " + summary.firstRoomPayableAvg()
        textClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun onClickCheckBox(view: View) {
        if (view is RadioButton) {
            when (view.id) {
                R.id.redeem_check_box -> {
                    onEarnTripCoinChecked()
                }

                R.id.radio_button_card_payment -> {
                    isCardSelected.set(true)
                    isRedeemSelected.set(false)
                    isCouponSelected.set(false)
                    wannaRedeem.set(false)
                    cardPaymentChecked.value = true
                }

                R.id.radio_button_coupon -> {
                    wannaRedeem.set(false)
                    isCardSelected.set(false)
                    isRedeemSelected.set(false)
                    isCouponSelected.set(true)
                    availCoupon.value = true
                }
            }
        }
    }

    private fun onEarnTripCoinChecked() {
        hotelEventManager.redeemTripCoinOnHotelBooking()
        isCardSelected.set(false)
        isRedeemSelected.set(true)
        isCouponSelected.set(false)
        wannaRedeem.set(true)
        redeemChecked.value = true
    }

    fun onCouponApply() {
        val couponRequest = couponObserver.get()
        if (preVCouponCode.isEmpty() || preVCouponCode != couponRequest?.coupon) {
            preVCouponCode = couponRequest?.coupon.toString()
            couponRequest?.let {
                it.deviceType = "Android"
                it.serviceType = "Hotel"
                it.extraParams =
                    HotelExtraParams(
                        summary.searchId!!,
                        summary.providerCode,
                        summary.hotelId!!,
                        summary.rooms,
                        summary.roomSearchCode!!,
                        summary.propertyRoomId!!
                    )
                onCouponRequest(it)
            }
        } else
            showMessage(UtilText.alreadyApplied)
    }

    private fun onCouponRequest(couponReq: HotelCouponRequest) {
        dataLoading.set(true)
        hideKeyboard.call()
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(HotelDiscountApiCallingKey.CouponRequest.name) {
            apiService.getValidateHotelCoupon(
                token,
                couponReq
            )
        }
    }

    private fun updateCouponDetailsUI(couponCode: String, data: CouponResponse) {
        when (data.discountType) {
            CouponType.FLAT.value ->
                couponDiscount = data.discount.toDouble()

            CouponType.PERCENTAGE.value ->
                couponDiscount =
                    ((data.discount.toDouble() / 100) * summary.totalPayableAfterDiscount()).twoDigitDecimal()
        }

        when (data.withDiscount) {
            CouponDiscountStatus.NO.value -> {
                discountAmount.set(0)
                discountModel.couponWithDiscount = false
            }
            CouponDiscountStatus.YES.value -> {
                discountAmount.set(ceil(summary.totalDiscount()).toInt())
                discountModel.couponWithDiscount = true
            }
        }

        if (!isGPStarCouponShow.get())
            discountModel.coupon = couponCode

        discountModel.discountAmount = couponDiscount
        extraDiscountAmount.set(ceil(couponDiscount).toInt())
        calculatePayableAmount()

        if (!isGPStarCouponShow.get()) {
            isGPCouponApplied = false
            isCouponApplied.set(true)
            showMessage(COUPON_ADDED_SUCCESSFULLY)
        }
    }

    private fun counter(seconds: Long = ShareTripAppConstants.COUNTDOWN_TIME) {
        timer = object : CountDownTimer(
            seconds,
            ShareTripAppConstants.COUNTDOWN_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val min = (millisUntilFinished / 1000) / 60
                val sec = (millisUntilFinished / 1000) % 60
                countdownTimerOrResendText.set(
                    String.format(
                        ShareTripAppConstants.COUNTDOWN_FORMAT,
                        min,
                        sec
                    )
                )
            }

            override fun onFinish() {
                countdownTimerOrResendText.set(UtilText.otpResend)
            }
        }
        timer.start()
    }

    private fun gpLoyaltyCheck(phoneNo: String) {
        dataLoading.set(true)
        gpStarNumber = phoneNo
        executeSuspendedCodeBlock(HotelDiscountApiCallingKey.GpLoyaltyCheck.name) {
            DataManager.hotelApiService.gpLoyaltyCheck(
                token,
                GpLoyaltyCheckRequest(mobileNumber = phoneNo)
            )
        }
    }

    private fun otpVerification(phoneNo: String, otp: String) {
        dataLoading.set(true)
        verifiedOTP = otp
        executeSuspendedCodeBlock(HotelDiscountApiCallingKey.OTPVerify.name) {
            DataManager.hotelApiService.verifyOTP(
                token,
                VerifyOTPRequest(mobileNumber = phoneNo, otp = otp)
            )
        }
    }

    private fun updateFinalPayableWithConvenience(payableAmount: Double) {
        val finalPayableAmount =
            if (allPaymentMethod.value.isNullOrEmpty() || allPaymentMethod.value!![0].charge == 0.0) {
                totalConvenienceCharge.set(0)
                vatCharge.set(0)
                isConvenienceVisible.set(false)
                payableAmount
            } else {
                val convenienceData = calculateConvenience(payableAmount)
                totalConvenienceCharge.set(ceil(convenienceData.first).toInt())
                vatCharge.set(ceil(convenienceData.second).toInt())
                isConvenienceVisible.set(true)
                convenienceData.third
            }

        totalPrice.set(ceil(finalPayableAmount).toInt())
    }

    private fun calculateConvenience(totalAmount: Double): Triple<Double, Double, Double> {
        val convenience = (allPaymentMethod.value!![0].charge / 100) * totalAmount
        val vat = if (convenienceVatFlag) {
            0.05 * convenience
        } else {
            0.0
        }

        return Triple(convenience, vat, totalAmount + convenience + vat)
    }

    fun onResendOTPClick() {
        if (countdownTimerOrResendText.get() == UtilText.otpResend) {
            gpLoyaltyCheck(gpStarNumber)
        }
    }

    fun onCouponChecked() {
        isCouponSelected.set(true)
        isCardSelected.set(false)
        isRedeemSelected.set(false)
        isCouponSelected.set(true)
        if (discountModel.couponWithDiscount) {
            discountAmount.set(ceil(summary.totalDiscount()).toInt())
        } else {
            discountAmount.set(0)
        }
        extraDiscountAmount.set(ceil(couponDiscount).toInt())
        isExtraDiscountVisible.set(true)
        extraDiscountLabel.set("Redeem Coupon")
        calculatePayableAmount()
        isRedeemCodeActivated = false
        observableUserTripCoin.value =
            NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
        discountModel.disCountMethod = "Coupon"
        discountModel.discountAmount = couponDiscount
    }

    fun onRedeemChecked() {
        extraDiscountAmount.set(ceil(redeemCoin.get().toDouble()).toInt())
        discountAmount.set(0)
        isExtraDiscountVisible.set(true)
        extraDiscountLabel.set("Redeem Coins")
        calculatePayableAmount()
        isRedeemCodeActivated = true
        observableUserTripCoin.value =
            NumberFormat.getNumberInstance(Locale.US).format((userTripCoin - progressForTripCoin))
        discountModel.disCountMethod = "Coin"
    }

    fun onCardChecked() {
        isCardSelected.set(true)
        isRedeemSelected.set(false)
        isCouponSelected.set(false)
        wannaRedeem.set(false)
        extraDiscountAmount.set(0)
        discountAmount.set(ceil(summary.totalDiscount()).toInt())
        isExtraDiscountVisible.set(false)
        calculatePayableAmount()
        isRedeemCodeActivated = false
        observableUserTripCoin.value =
            NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
        discountModel.disCountMethod = "Card"
        discountModel.discountAmount = summary.totalDiscount()
    }

    private fun fetchPaymentGateWay() {
        dataLoading.set(true)
        executeSuspendedCodeBlock(HotelDiscountApiCallingKey.PaymentGateWay.name) {
            apiService.fetchPaymentGateway(GatewayService.Hotel.name, summary.gatewayType)
        }
    }

    fun fetchUserProfile() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        dataLoading.set(true)
        executeSuspendedCodeBlock(HotelDiscountApiCallingKey.UserProfile.name) {
            apiService.getUserInformation(token)
        }
    }

    fun calculatePayableAmount() {
        val disCountAmountValue = discountAmount.get() + extraDiscountAmount.get()
        val payableAmountValue = summary.totalPayableBeforeDiscount()
        if (disCountAmountValue >= payableAmountValue) {
            totalPrice.set(0)
        } else {
            val finalPayableAmount =
                (summary.totalPayableBeforeDiscount() - disCountAmountValue).twoDigitDecimal()

            updateFinalPayableWithConvenience(finalPayableAmount)
        }
    }

    fun setCoupon(couponName: String) {
        couponObserver.set(HotelCouponRequest().apply {
            coupon = couponName
        })
    }

    fun onCouponSelected(coupon: PromotionalCoupon) {
        setCoupon(coupon.coupon)
    }

    fun onVerifyClick() {
        if (couponState.value != GPCouponInputState.OTPInputState.name && !dataLoading.get()) {
            if (inputObserver.get().isPhoneNumberValid()) {
                gpLoyaltyCheck(inputObserver.get().toString())
            } else {
                showMessage(UIMessageData.PLEASE_PROVIDE_A_VALID_PHONE_NUMBER)
            }
        } else {
            otpVerification(gpStarNumber, inputObserver.get().toString())
        }
    }

    fun useAnotherNumber() {
        gpCouponInputHint.set(UtilText.enterPhone)
        couponState.value = GPCouponInputState.MobileInputState.name
        if (::timer.isInitialized) timer.cancel()
        countdownTimerOrResendText.set("")
        inputObserver.set("")
    }

    override fun onClickLogin() {
        isDismissDialog.value = true
        navigateToLogin.value = true
    }
}
