package net.sharetrip.hotel.booking.view.summary

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
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.PrefKey.USER_TRIP_COIN
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
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
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.network.PaymentGatewayType
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.hotel.utils.MsgUtils.COUPON_ADDED_SUCCESSFULLY
import net.sharetrip.hotel.utils.MsgUtils.PLEASE_APPLY_A_COUPON_OR_SELECT_ANOTHER_OPTION
import net.sharetrip.hotel.utils.MsgUtils.SELECT_PAYMENT_METHOD
import net.sharetrip.hotel.utils.MsgUtils.TERMS_AND_CONDITION
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.model.user.User
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class BookingSummaryViewModel(
    private val hotelBookingParams: HotelBookingParams,
    private val roomSummary: RoomBookingSummary,
    var discountModel: DiscountModel,
    promotionalCoupons: ListOfCoupon,
    private var apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private var token = ""
    private val isCheckboxActive = ObservableBoolean()
    private var gateWayId = ""
    private var userTripCoin = 0
    private var couponDiscount = 0.0
    private var hotelCoupon: String = ""
    private val convenienceVatFlag = false
    private lateinit var timer: CountDownTimer
    private val couponGatewayList = MutableLiveData<List<String>>()
    private val hotelEventManager =
        AnalyticsProvider.hotelEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private var verifiedOTP = ""
    private var preVCouponCode = ""
    private var couponReq: HotelCouponRequest? = null
    private var isCouponApplied = ObservableBoolean(false)
    val summary = ObservableField<RoomBookingSummary>()

    val couponList = MutableLiveData<List<PromotionalCoupon>>()
    val radioOption = MutableLiveData<DiscountOption>()
    val starRatingLiveData = MutableLiveData<Int>()
    val seekBarProgress = MutableLiveData<Double>()

    val isCardSelected = ObservableBoolean(true)
    val isRedeemSelected = ObservableBoolean(false)
    val isCouponSelected = ObservableBoolean(false)
    val isCouponShow = ObservableBoolean(true)
    val clickBooking = MutableLiveData<Event<Boolean>>()
    val discountAmount = ObservableInt()
    val redeemCoin = ObservableInt()
    val totalPrice = ObservableInt()
    val wannaRedeem = ObservableBoolean()
    val searchSummary = ObservableField<SearchSummary>()
    val couponObserver = ObservableField<HotelCouponRequest>()
    val isDiscountOptionExpand = ObservableBoolean(false)
    val isFlightSummaryExpand = ObservableBoolean(false)
    val discountName = ObservableField<String>()
    var cardSeries = String()
    val earnText = ObservableField<String>()
    val redeemText = ObservableField<String>()
    val couponText = ObservableField<String>()
    val currency = ObservableField(PaymentGatewayType.BDT.name)
    var conversionRate = 0.0
    var payableButtonText = ObservableField("Pay Now")

    val isConvenienceVisible = ObservableBoolean(false)
    val totalConvenienceCharge = ObservableField(0)
    val vatCharge = ObservableField(0)
    val selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    val observableUserTripCoin = SingleLiveEvent<String>()
    val hideKeyboard = SingleLiveEvent<Any>()
    val navigateToPaymentScreen = SingleLiveEvent<Bundle>()

    val navigateToPrivacyPolicy = SingleLiveEvent<Any>()
    val navigateToTermsAndCondition = SingleLiveEvent<Any>()
    var isExtraDiscountVisible = ObservableBoolean(false)
    var extraDiscountLabel = ObservableField<String>()

    val extraDiscountAmount = ObservableInt()

    val paymentList = MutableLiveData<Event<ArrayList<PaymentMethod>>>()
    val filteredPaymentList = MutableLiveData<List<PaymentMethod>>()
    val paymentMethodWithEarn: MutableList<PaymentMethod> = ArrayList()

    val paymentMethodWithRedeem: MutableList<PaymentMethod> = ArrayList()
    val paymentMethodWithCoupon: MutableList<PaymentMethod> = ArrayList()
    val isGPStarCouponShow = ObservableBoolean(false)
    var gpCouponTitle = ObservableField(UtilText.couponTitle)
    var gpCouponSubTitle = ObservableField(UtilText.couponSubTitle)
    val countdownTimerOrResendText = ObservableField<String>()
    val couponState = MutableLiveData(GPCouponInputState.MobileInputState.name)
    val inputObserver = ObservableField<String>()
    var gpStarNumber = ""
    var gpCouponInputHint = ObservableField(UtilText.enterPhone)
    val isGpStarCouponVerified = MutableLiveData<Boolean>()

    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (progress > userTripCoin) {
                seekBar.progress = userTripCoin
                return
            }

            if (currency.get() == PaymentGatewayType.USD.name) {
                val book = roomSummary
                book.conversionRate = conversionRate
                redeemCoin.set(progress)
                extraDiscountAmount.set(ceil(progress / conversionRate).toInt())
                calculatePayableAmount()
                return
            }

            observableUserTripCoin.value =
                NumberFormat.getNumberInstance(Locale.US).format(userTripCoin - progress)
            redeemCoin.set(progress)
            extraDiscountAmount.set(ceil(progress / conversionRate).toInt())
            calculatePayableAmount()
        }
    }

    init {
        token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        hotelEventManager.initialCheckoutHotel()
        summary.set(roomSummary)
        preVCouponCode = discountModel.coupon

        if (preVCouponCode.isNotEmpty())
            isCouponApplied.set(true)

        val listType = object : TypeToken<List<RemoteDiscountModel>>() {}.type
        val remoteDiscountModel = Gson().fromJson<List<RemoteDiscountModel>>(
            sharedPrefsHelper[PrefKey.HOTEL_DISCOUNT_OPTIONS, PrefKey.DEFAULT_HOTEL_DISCOUNT_OPTION],
            listType
        )

        earnText.set(remoteDiscountModel.find { it.type == EARN }?.title)
        redeemText.set(remoteDiscountModel.find { it.type == REDEEM }?.title)
        couponText.set(remoteDiscountModel.find { it.type == COUPON }?.title)
        redeemCoin.set(0)
        dataLoading.set(true)
        wannaRedeem.set(false)
        couponObserver.set(HotelCouponRequest())

        val summaryStr = sharedPrefsHelper[PrefKey.HOTEL_SEARCH_SUMMARY, ""]
        val summary = MoshiUtil.convertStringToHotelSummary(summaryStr)

        hotelCoupon = discountModel.coupon
        searchSummary.set(summary)

        if (summary.starRating != "" && summary.starRating != "0") {
            starRatingLiveData.value = summary.starRating.toInt()
        }

        if (promotionalCoupons.couponList.isNullOrEmpty())
            isCouponShow.set(false)

        couponDiscount = discountModel.discountAmount

        when (discountModel.disCountMethod) {
            "Coin" -> {
                radioOption.value = DiscountOption.COIN
            }
            "Coupon" -> {
                radioOption.value = DiscountOption.COUPON
            }
            "Card" -> {
                radioOption.value = DiscountOption.CARD
            }
        }

        setPricingDetails()
        fetchUserProfile()
        fetchPaymentGateWay()
        couponList.value = promotionalCoupons.couponList
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        when (operationTag) {
            HotelSummaryApiCallingKey.PAYMENT_GATEWAY.name -> {
                val response = (data.body as RestResponse<*>).response as List<PaymentMethod>

                separatePaymentList(response as ArrayList<PaymentMethod>)
                paymentList.value = Event(response)

                when (discountModel.disCountMethod) {
                    "Coupon" -> {
                        couponDiscount = discountModel.discountAmount
                        val couponRequest = HotelCouponRequest()
                        couponRequest.let {
                            it.deviceType = "Android"
                            it.serviceType = "Hotel"
                            it.coupon = discountModel.coupon
                            couponObserver.set(couponRequest)
                        }
                        radioOption.value = DiscountOption.COUPON
                    }
                    "Coin" -> {
                        redeemCoin.set(discountModel.discountAmount.toInt())
                        seekBarProgress.value = discountModel.discountAmount
                        radioOption.value = DiscountOption.COIN
                    }
                    else -> {
                        radioOption.value = DiscountOption.CARD
                    }
                }

            }

            HotelSummaryApiCallingKey.USER_PROFILE.name -> {
                val response = (data.body as RestResponse<*>).response as User
                sharedPrefsHelper.put(
                    USER_TRIP_COIN,
                    response.totalPoints.toString()
                )
                observableUserTripCoin.value = NumberFormat.getNumberInstance(Locale.US)
                    .format(response.totalPoints)
                userTripCoin = response.totalPoints
            }

            HotelSummaryApiCallingKey.BOOK_HOTEL.name -> {
                val response = (data.body as RestResponse<*>).response as RoomBookingResponse
                val bundle = Bundle()
                bundle.putString(ARG_PAYMENT_URL, response.url)
                bundle.putString(SERVICE_TYPE, SERVICE_TYPE_HOTEL)
                navigateToPaymentScreen.value = bundle
            }

            HotelSummaryApiCallingKey.COUPON_REQUEST.name -> {
                val response = (data.body as RestResponse<*>).response as CouponResponse
                couponReq?.let {
                    hotelCoupon = couponReq?.coupon!!
                }

                isGPStarCouponShow.set(
                    response.mobileVerificationRequired?.lowercase().equals("yes")
                )

                couponObserver.get()?.coupon?.let {
                    updateCouponDetailsUI(response.discount, response)
                }

                if (isGPStarCouponShow.get()) {
                    isCouponApplied.set(false)
                    isGpStarCouponVerified.postValue(true)
                    showMessage(UtilText.gpNumberVerify)
                }

                couponGatewayList.value = response.gateway
                filterCouponGateway(response.gateway)

                response.withDiscount.let {
                    discountModel.couponWithDiscount = it == "Yes"
                }
            }

            HotelDiscountApiCallingKey.GpLoyaltyCheck.name -> {
                dataLoading.set(false)
                val response = (data.body as RestResponse<*>).response as GPLoyaltyCheckResponse
                if (response.success == true) {
                    hotelBookingParams.verifiedMobileNumber = gpStarNumber
                    inputObserver.set("")
                    gpCouponInputHint.set(UtilText.enterOtp)
                    showMessage(UtilText.enterOtp)
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
                timer.cancel()
                hotelBookingParams.otp = verifiedOTP
                countdownTimerOrResendText.set("")
                couponState.value = GPCouponInputState.MobileInputState.name
                gpCouponInputHint.set(UtilText.enterPhone)
                isGPStarCouponShow.set(false)
                showMessage((data.body as RestResponse<*>).message)
                dataLoading.set(false)
                isCouponApplied.set(true)
                showMessage(COUPON_ADDED_SUCCESSFULLY)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun separatePaymentList(paymentMethods: ArrayList<PaymentMethod>) {
        paymentMethodWithEarn.addAll(paymentMethods.filter { it.isEarnTripCoinApplicable })
        paymentMethodWithRedeem.addAll(paymentMethods.filter { it.isRedeemTripCoinApplicable })
        paymentMethodWithCoupon.addAll(paymentMethods.filter { it.isCouponAvailable })
    }

    private fun calculateConvenience(totalAmount: Int): Triple<Double, Double, Double> {
        val convenience = (selectedPaymentMethod.value?.charge!! / 100) * totalAmount
        val vat = if (convenienceVatFlag) {
            0.05 * convenience
        } else {
            0.0
        }

        return Triple(convenience, vat, totalAmount + convenience + vat)
    }

    private fun setPayButtonText() {
        if (totalPrice.get() == 0) {
            payableButtonText.set("Book Now")
        } else {
            payableButtonText.set("Pay Now")
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

    private fun fetchUserProfile() {
        dataLoading.set(true)

        executeSuspendedCodeBlock(HotelSummaryApiCallingKey.USER_PROFILE.name) {
            apiService.getUserInformation(token)
        }
    }

    private fun fetchPaymentGateWay() {
        dataLoading.set(true)

        executeSuspendedCodeBlock(HotelSummaryApiCallingKey.PAYMENT_GATEWAY.name) {
            apiService.fetchPaymentGateway(GatewayService.Hotel.name, summary.get()!!.gatewayType)
        }
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

    fun onResendOTPClick() {
        if (countdownTimerOrResendText.get() == UtilText.otpResend) {
            gpLoyaltyCheck(gpStarNumber)
            counter()
        }
    }

    fun filterCouponGateway(gatewayList: List<String>) {
        val selectedList = mutableListOf<PaymentMethod>()
        gatewayList.forEach { gateway ->
            paymentMethodWithCoupon.forEach { paymentMethod ->
                if (gateway == paymentMethod.id) {
                    selectedList.add(paymentMethod)
                }
            }
        }

        if (selectedList.isEmpty()) {
            selectedList.addAll(paymentMethodWithCoupon)
        }
        filteredPaymentList.postValue(selectedList)
    }

    fun bookAHotel() {
        hotelBookingParams.gatewayId = gateWayId
        hotelBookingParams.cardSeries = cardSeries
        hotelBookingParams.tripCoin = redeemCoin.get().toString()
        hotelBookingParams.coupon = if (isCouponSelected.get()) hotelCoupon else ""

        if (!dataLoading.get()) {
            dataLoading.set(true)
            executeSuspendedCodeBlock(HotelSummaryApiCallingKey.BOOK_HOTEL.name) {
                apiService.getHotelBookingResponse(token, hotelBookingParams)
            }
        }
    }

    fun noCardSeriesAvailable() {
        isCouponShow.set(false)
    }

    fun onCouponApply() {
        couponReq = couponObserver.get()
        if (preVCouponCode.isEmpty() || preVCouponCode != couponReq?.coupon) {
            preVCouponCode = couponReq?.coupon!!
            couponReq?.apply {
                serviceType = "Hotel"
                deviceType = "Android"
                extraParams = HotelExtraParams(
                    summary.get()!!.searchId!!,
                    summary.get()!!.providerCode,
                    summary.get()!!.hotelId!!,
                    summary.get()!!.rooms,
                    summary.get()!!.roomSearchCode!!,
                    summary.get()!!.propertyRoomId!!
                )
                onCouponRequest(this)
            }
        } else
            showMessage(UtilText.alreadyApplied)
    }

    fun setPaymentGateWayId(id: String) {
        gateWayId = id
    }

    fun onClickCheckBox(view: View) {
        if (view is RadioButton) {
            when (view.id) {
                R.id.redeem_check_box -> radioOption.value = DiscountOption.COIN

                R.id.radio_button_card_payment -> radioOption.value = DiscountOption.CARD

                R.id.radio_button_coupon -> radioOption.value = DiscountOption.COUPON
            }
        }
    }

    fun onCardChecked() {
        discountName.set(earnText.get())
        discountAmount.set(ceil(summary.get()!!.totalDiscount()).toInt())
        extraDiscountAmount.set(0)
        isExtraDiscountVisible.set(false)
        calculatePayableAmount()
        isCardSelected.set(true)
        isRedeemSelected.set(false)
        isCouponSelected.set(false)
        wannaRedeem.set(false)
    }

    fun onRedeemChecked() {
        discountName.set(redeemText.get())
        discountAmount.set(0)
        extraDiscountAmount.set(ceil(redeemCoin.get().toDouble()).toInt())
        extraDiscountLabel.set("Redeem Coins")
        isExtraDiscountVisible.set(true)
        calculatePayableAmount()
        isCardSelected.set(false)
        isRedeemSelected.set(true)
        isCouponSelected.set(false)
        wannaRedeem.set(true)
    }

    fun onCouponChecked() {
        if (discountModel.couponWithDiscount) {
            discountAmount.set(ceil(summary.get()!!.totalDiscount()).toInt())
        } else {
            discountAmount.set(0)
        }

        discountName.set(couponText.get())
        extraDiscountAmount.set(ceil(couponDiscount).toInt())
        isExtraDiscountVisible.set(true)
        extraDiscountLabel.set("Redeem Coupon")
        calculatePayableAmount()
        wannaRedeem.set(false)
        isCardSelected.set(false)
        isRedeemSelected.set(false)
        isCouponSelected.set(true)
    }

    private fun onCouponRequest(couponReq: HotelCouponRequest) {
        hideKeyboard.call()
        dataLoading.set(true)

        executeSuspendedCodeBlock(HotelSummaryApiCallingKey.COUPON_REQUEST.name) {
            apiService.getValidateHotelCoupon(token, couponReq)
        }
    }

    private fun updateCouponDetailsUI(discount: String, couponResponse: CouponResponse) {
        if (!isGPStarCouponShow.get()) {
            isCouponApplied.set(true)
            showMessage(COUPON_ADDED_SUCCESSFULLY)
        }

        when (couponResponse.discountType) {
            CouponType.FLAT.value -> couponDiscount = discount.toDouble()
            CouponType.PERCENTAGE.value -> couponDiscount =
                ((discount.toDouble() / 100) * summary.get()!!.totalPayableAfterDiscount())
        }

        when (couponResponse.withDiscount) {
            CouponDiscountStatus.NO.value -> {
                discountAmount.set(0)
            }
            CouponDiscountStatus.YES.value -> {
                discountAmount.set(ceil(summary.get()!!.totalDiscount()).toInt())
            }
        }

        extraDiscountAmount.set(ceil(couponDiscount).toInt())
        calculatePayableAmount()
        onCouponChecked()
    }


    private fun setPricingDetails() {
        val discount = getDiscountAmountInBDT().first
        val extraDiscount = getDiscountAmountInBDT().second

        if (currency.get() == PaymentGatewayType.USD.name) {
            val usdDiscount = discount / conversionRate
            val usdExtraDiscount = extraDiscount / conversionRate
            discountAmount.set(ceil(usdDiscount).toInt())
            extraDiscountAmount.set(ceil(usdExtraDiscount).toInt())
        } else {
            discountAmount.set(ceil(discount).toInt())
            extraDiscountAmount.set(ceil(extraDiscount).toInt())
        }

        calculatePayableAmount()
    }

    private fun getDiscountAmountInBDT(): Pair<Double, Double> {
        return when {
            isCouponSelected.get() -> {
                if (isCouponApplied.get()) {
                    if (discountModel.couponWithDiscount) {
                        Pair(
                            roomSummary.totalDiscount(),
                            couponDiscount
                        )
                    } else {
                        Pair(0.0, couponDiscount)
                    }
                } else {
                    Pair(roomSummary.totalDiscount(), couponDiscount)
                }
            }
            isRedeemSelected.get() -> {
                Pair(0.0, redeemCoin.get().toDouble())
            }
            isCardSelected.get() -> {
                Pair(roomSummary.totalDiscount(), 0.0)
            }
            else -> {
                Pair(0.0, 0.0)
            }
        }
    }

    private fun updateFinalPayableWithConvenience(payableAmount: Int) {
        val finalPayableAmount =
            if (selectedPaymentMethod.value?.charge == null || selectedPaymentMethod.value?.charge == 0.0) {
                totalConvenienceCharge.set(0)
                vatCharge.set(0)
                isConvenienceVisible.set(false)
                payableAmount
            } else {
                val convenienceData = calculateConvenience(payableAmount)
                totalConvenienceCharge.set(ceil(convenienceData.first).toInt())
                vatCharge.set(ceil(convenienceData.second).toInt())
                isConvenienceVisible.set(true)
                ceil(convenienceData.third).toInt()
            }

        totalPrice.set(
            finalPayableAmount
        )
    }

    fun onChangeBtnClick() {
        isDiscountOptionExpand.set(!isDiscountOptionExpand.get())
    }

    fun onSummaryLayoutClick() {
        isFlightSummaryExpand.set(!isFlightSummaryExpand.get())
    }

    @SuppressLint("SetTextI18n")
    fun onInformationIconClick(view: View) {
        val dialog = Dialog(view.context, R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_hotel_rate_info)
        val perDayLayout: LinearLayout = dialog.findViewById(R.id.perDayLayout)
        val totalPriceHints: AppCompatTextView = dialog.findViewById(R.id.totalPriceHints)
        val totalPrice: AppCompatTextView = dialog.findViewById(R.id.totalPrice)
        val avgPrice: AppCompatTextView = dialog.findViewById(R.id.avgPrice)
        val textClose: AppCompatTextView = dialog.findViewById(R.id.textClose)

        summary.get()!!.dateList.forEach {
            val layoutView = LayoutInflater.from(perDayLayout.context)
                .inflate(R.layout.hotel_per_day_price_layout, null, true)
            val dayName: AppCompatTextView = layoutView.findViewById(R.id.dayName)
            val dayPrice: AppCompatTextView = layoutView.findViewById(R.id.dayPrice)
            dayName.text = summary.get()!!.totalRoom.toString() + " X " + it
            dayPrice.text = summary.get()!!.firstRoomPayablePerNight()
            perDayLayout.addView(layoutView)
        }

        totalPriceHints.text = "Total for ${summary.get()!!.totalNight} Night(s)"
        totalPrice.text = currency.get() + " " + summary.get()!!.firstRoomPayable()
        avgPrice.text = currency.get() + " " + summary.get()!!.firstRoomPayableAvg()
        textClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun onClickTermsAndConditionCheckbox(view: View) {
        isCheckboxActive.set((view as AppCompatCheckBox).isChecked)
    }

    fun onClickPrivacyPolicy() {
        navigateToPrivacyPolicy.call()
    }

    fun onClickTermsAndCondition() {
        navigateToTermsAndCondition.call()
    }

    fun updateUSDPayment() {
        currency.set(PaymentGatewayType.USD.name)
        roomSummary.conversionRate = selectedPaymentMethod.value?.currency?.conversion?.rate!!
        conversionRate = selectedPaymentMethod.value?.currency?.conversion?.rate!!
        summary.set(roomSummary)
        summary.notifyChange()
        setPricingDetails()
    }

    fun updateWithBDTPayment() {
        currency.set(PaymentGatewayType.BDT.name)
        roomSummary.conversionRate = selectedPaymentMethod.value?.currency?.conversion?.rate!!
        conversionRate = selectedPaymentMethod.value?.currency?.conversion?.rate!!
        summary.set(roomSummary)
        summary.notifyChange()
        setPricingDetails()
    }

    fun setCoupon(couponName: String) {
        couponObserver.set(HotelCouponRequest().apply {
            coupon = couponName
        })
    }

    fun onClickBookNow() {
        when {
            radioOption.value == DiscountOption.CARD && gateWayId.isEmpty() -> {
                showMessage(SELECT_PAYMENT_METHOD)
            }
            !isCheckboxActive.get() -> {
                showMessage(TERMS_AND_CONDITION)
            }
            radioOption.value == DiscountOption.COUPON && (hotelCoupon.isEmpty() || !isCouponApplied.get()) -> {
                showMessage(PLEASE_APPLY_A_COUPON_OR_SELECT_ANOTHER_OPTION)
            }
            else -> {
                clickBooking.value = Event(true)
            }
        }
    }

    fun calculatePayableAmount() {
        val disCountAmountValue = discountAmount.get() + extraDiscountAmount.get()
        val payableAmountValue = summary.get()!!.totalPayableBeforeDiscount()
        if (disCountAmountValue >= payableAmountValue) {
            totalPrice.set(0)
        } else {
            val finalPayableAmount = (summary.get()!!
                .totalPayableBeforeDiscount() - disCountAmountValue).twoDigitDecimal()

            updateFinalPayableWithConvenience(ceil(finalPayableAmount).toInt())
        }
        setPayButtonText()
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
}
