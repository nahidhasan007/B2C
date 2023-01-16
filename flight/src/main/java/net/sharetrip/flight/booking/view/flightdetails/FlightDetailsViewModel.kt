package net.sharetrip.flight.booking.view.flightdetails

import android.os.CountDownTimer
import android.view.View
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.PrefKey.ACCESS_TOKEN
import com.sharetrip.base.data.PrefKey.USER_TRIP_COIN
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.GatewayService
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.utils.ShareTripAppConstants.COUNTDOWN_FORMAT
import com.sharetrip.base.utils.ShareTripAppConstants.COUNTDOWN_INTERVAL
import com.sharetrip.base.utils.ShareTripAppConstants.COUNTDOWN_TIME
import net.sharetrip.shared.utils.UtilText
import net.sharetrip.shared.utils.UtilText.couponSubTitle
import net.sharetrip.shared.utils.UtilText.couponTitle
import net.sharetrip.shared.utils.UtilText.enterPhone
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.shared.utils.isPhoneNumberValid
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.*
import net.sharetrip.flight.booking.model.coupon.*
import net.sharetrip.flight.booking.model.flightresponse.DiscountModel
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.model.luggage.*
import net.sharetrip.flight.booking.model.payment.COUPON
import net.sharetrip.flight.booking.model.payment.EARN
import net.sharetrip.flight.booking.model.payment.REDEEM
import net.sharetrip.flight.booking.model.payment.RemoteDiscountModel
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.COUPON_ADDED_SUCCESSFULLY
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.PLEASE_PROVIDE_A_VALID_PHONE_NUMBER
import net.sharetrip.flight.booking.model.rulemodel.AirFareResponse
import net.sharetrip.flight.booking.model.user.User
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.network.FlightBookingApiService
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.tracker.utils.PLEASE_SELECT_A_VALID_COUPON
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class FlightDetailsViewModel(
    private val flightSearch: FlightSearch,
    private val flights: Flights,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val apiService: FlightBookingApiService
) : BaseOperationalViewModel(), GuestLoginListener {
    private var flag = false
    private var userTripCoin: Int
    private var isRedeemCodeActivated = false
    private var progressForTripCoin: Int = 0
    private var couponDiscountAmount = 0.0
    private var flightCoupon: String = ""
    private var amount: String = ""
    private var travellerBaggageCodeList: ArrayList<TravellerBaggageCode> = ArrayList()
    private var isLuggageOptional = true
    private lateinit var currentCouponRequest: FlightCouponRequest
    private val convenienceVatFlag = false
    private var baggageType: BaggageType = BaggageType.Unknown
    private val flightEventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private lateinit var flightDetails: FlightDetailsResponse

    var couponResponse = CouponResponse()
    val discountModel = DiscountModel()
    val ruleCase = MutableLiveData<Event<Int>>()

    val redeemCoin = ObservableInt()
    val wannaRedeem = ObservableBoolean()

    var bankDiscountInfo: String = ""
    val isCouponShow = ObservableBoolean(true)
    val couponObserver = ObservableField<FlightCouponRequest>()
    val isCardSelected = ObservableBoolean(false)
    val isRedeemSelected = ObservableBoolean(false)
    val isCouponSelected = ObservableBoolean(true)
    val earnText = ObservableField<String>()
    val redeemText = ObservableField<String>()

    val couponText = ObservableField<String>()
    val redeemChecked = MutableLiveData<Boolean>()
    val cardPaymentChecked = MutableLiveData<Boolean>()
    val availCoupon = MutableLiveData<Boolean>()

    val isDismissDialog = MutableLiveData<Boolean>()
    var isLogin = ObservableBoolean()
    val luggageResponse = MutableLiveData<LuggageResponse>()
    var isBaggageDataLoading = ObservableBoolean(false)
    var isBaggageDataAvailable = ObservableBoolean(false)
    var isBaggageExpanded = ObservableBoolean(true)
    val flightCouponList = MutableLiveData<List<PromotionalCoupon>>()
    var rules = arrayListOf(
        flightSearch.baggageDetails,
        flightSearch.airFareRulesDetails,
        getFarePolicyDetails()
    )
    val isDataLoading = MutableLiveData<Boolean>()
    val gotoSegment = MutableLiveData<Event<Int>>()
    val gotoCheckout = MutableLiveData<Event<Boolean>>()
    val tripCoinText = MutableLiveData<String>()
    val isLoginClicked = MutableLiveData<Event<Boolean>>()
    val allPaymentMethod: MutableLiveData<List<PaymentMethod>> = MutableLiveData()
    val discountAmount = ObservableInt()
    val totalPrice = ObservableInt()
    val isConvenienceVisible = ObservableBoolean(false)
    val totalBaggageCharge = ObservableField(0)
    val totalConvenienceCharge = ObservableField(0)
    val vatCharge = ObservableField(0)
    val advanceIncomeTax = ObservableField(0)
    val totalPriceWithoutDiscount = ObservableInt()
    var isExtraDiscountVisible = ObservableBoolean(false)
    var extraDiscountLabel = ObservableField<String>()
    val extraDiscountAmount = ObservableInt()
    val initialLoading = ObservableBoolean(true)

    val couponState = MutableLiveData(GPCouponInputState.MobileInputState.name)
    val sendAgain = ObservableBoolean(false)
    val gpStarCouponVerificationNeeded = ObservableBoolean(false)
    var gpCouponTitle = ObservableField(couponTitle)
    var gpCouponSubTitle = ObservableField(couponSubTitle)
    var timerOrResendAction = ObservableField("")
    var gpCouponInputHint = ObservableField(enterPhone)
    private lateinit var timer: CountDownTimer
    var inputObserver = ObservableField<String>()
    var gpStarNumber = ""
    var verifiedOTP = ""


    val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.flight_body,
            R.drawable.ic_flight_blue, this
        )
    }

    val blurData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.discount_body,
            R.drawable.ic_discount_mono, this
        )
    }

    val isShowDialog: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val isLoginLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        wannaRedeem.set(false)
        couponObserver.set(FlightCouponRequest())
        amount = sharedPrefsHelper[USER_TRIP_COIN, ""]
        amount = amount.filter { it in '0'..'9' }

        userTripCoin = if (amount.isEmpty()) {
            0
        } else {
            amount.toInt()
        }

        advanceIncomeTax.set(ceil(flights.priceBreakdown.advanceIncomeTax).toInt())
        totalPriceWithoutDiscount.set(ceil(getOriginPrice()).toInt())
        discountAmount.set(ceil(flights.priceBreakdown.getDiscount()).toInt())
        bankDiscountInfo = sharedPrefsHelper[PrefKey.FLIGHT_DISCOUNT_OFFER_BANK_LIST, ""]
        discountModel.type = DiscountType.CARD.value
        discountModel.discountAmount = flights.priceBreakdown.getDiscount()
        val listType = object : TypeToken<List<RemoteDiscountModel>>() {}.type

        val flightDiscountModel = Gson().fromJson<List<RemoteDiscountModel>>(
            sharedPrefsHelper[PrefKey.FLIGHT_DISCOUNT_OPTIONS, PrefKey.DEFAULT_FLIGHT_DISCOUNT_OPTION],
            listType
        )

        earnText.set(flightDiscountModel.find { it.type == EARN }?.title)
        redeemText.set(flightDiscountModel.find { it.type == REDEEM }?.title)
        couponText.set(flightDiscountModel.find { it.type == COUPON }?.title)
        getFlightDetails()
        checkLoginInformation()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            ApiCallingKey.GetFlightDetails.name -> {
                initialLoading.set(false)
                flightDetails = (data.body as RestResponse<*>).response as FlightDetailsResponse
                if (flightDetails.promotionalCoupon.isNotEmpty()) {
                    flights.availableCoupons = flightDetails.promotionalCoupon
                    flightCouponList.value = flightDetails.promotionalCoupon
                    couponSelection()
                } else {
                    onEarnTripCoinChecked()
                    isCouponShow.set(false)
                }
                loadInit()
            }
            ApiCallingKey.BaggageInformation.name -> {
                val baggageResponseObj =
                    ((data.body as RestResponse<*>).response) as LuggageResponse
                isBaggageDataLoading.set(false)
                prepareLuggageData(baggageResponseObj)
                isBaggageDataAvailable.set(true)
            }
            ApiCallingKey.Rule.name -> {
                flightSearch.airFareRules =
                    ((data.body as RestResponse<*>).response as AirFareResponse).airFareRules
                flightSearch.baggage =
                    ((data.body as RestResponse<*>).response as AirFareResponse).baggages
                flightSearch.fareDetails =
                    ((data.body as RestResponse<*>).response as AirFareResponse).fareDetails
            }
            ApiCallingKey.UserProfile.name -> {
                val tripCoin =
                    ((data.body as RestResponse<*>).response as User).totalPoints.toLong()
                sharedPrefsHelper.put(USER_TRIP_COIN, tripCoin)
                userTripCoin = tripCoin.toInt()
            }
            ApiCallingKey.CouponRequest.name -> {
                isDataLoading.value = false
                couponResponse = (data.body as RestResponse<*>).response as CouponResponse
                discountModel.coupon = currentCouponRequest.coupon
                discountModel.gateway = couponResponse.gateway

                couponResponse.withDiscount.let {
                    discountModel.couponWithDiscount = it == "Yes"
                }

                flightCoupon = currentCouponRequest.coupon
                updateCouponDetailsUI(couponResponse.discount, couponResponse.discountType)
                showMessage(COUPON_ADDED_SUCCESSFULLY)


                gpStarCouponVerificationNeeded.set(
                    couponResponse.mobileVerificationRequired?.lowercase().equals("yes")
                )

                if (gpStarCouponVerificationNeeded.get()) {
                    showMessage(UtilText.gpNumberVerify)
                }
            }
            ApiCallingKey.PaymentGateway.name -> {
                allPaymentMethod.postValue((data.body as RestResponse<*>).response as List<PaymentMethod>)
                calculateTotalPrice()
            }
            ApiCallingKey.FetchUserInfo.name -> {
                val userRestResponse = data.body as RestResponse<User>
                val tripCoin = NumberFormat.getNumberInstance(Locale.US)
                    .format(userRestResponse.response.totalPoints.toLong())
                tripCoinText.postValue(tripCoin)
                sharedPrefsHelper.put(USER_TRIP_COIN, tripCoin)
                amount = tripCoin.replace(",", "")
                userTripCoin = if (amount.isEmpty()) {
                    0
                } else {
                    amount.toInt()
                }
            }
            ApiCallingKey.GpLoyaltyCheck.name -> {
                initialLoading.set(false)
                val response = (data.body as RestResponse<*>).response as GPLoyaltyCheckResponse
                if (response.success == true) {
                    gpCouponInputHint.set(UtilText.enterOtp)
                    inputObserver.set("")
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
            ApiCallingKey.OTPVerify.name -> {
                timer.cancel()
                timerOrResendAction.set("")
                inputObserver.set("")
                couponState.value = GPCouponInputState.MobileInputState.name
                gpCouponInputHint.set(enterPhone)
                gpStarCouponVerificationNeeded.set(false)
                showMessage((data.body as RestResponse<*>).message)
                initialLoading.set(false)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            ApiCallingKey.BaggageInformation.name -> {
                isBaggageDataLoading.set(false)
                isBaggageDataAvailable.set(false)
                baggageType = BaggageType.Unknown
            }
            ApiCallingKey.CouponRequest.name -> {
                isDataLoading.value = false
                showMessage(errorMessage)
                flightCoupon = ""
            }
            ApiCallingKey.UserProfile.name -> {
                sharedPrefsHelper.put(USER_TRIP_COIN, "0")
            }
            ApiCallingKey.GetFlightDetails.name -> {
                showMessage(errorMessage)
                initialLoading.set(false)
            }
            ApiCallingKey.GpLoyaltyCheck.name -> {
                showMessage(errorMessage)
                initialLoading.set(false)
            }
            ApiCallingKey.OTPVerify.name -> {
                showMessage(errorMessage)
                initialLoading.set(false)
            }
        }
    }

    private fun gpLoyaltyCheck(phoneNo: String) {
        initialLoading.set(true)
        gpStarNumber = phoneNo
        val token = sharedPrefsHelper[ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(ApiCallingKey.GpLoyaltyCheck.name) {
            apiService.gpLoyaltyCheck(token, GpLoyaltyCheckRequest(mobileNumber = phoneNo))
        }
    }

    private fun otpVerification(phoneNo: String, otp: String) {
        verifiedOTP = otp
        initialLoading.set(true)
        val token = sharedPrefsHelper[ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(ApiCallingKey.OTPVerify.name) {
            apiService.verifyOTP(token, VerifyOTPRequest(mobileNumber = phoneNo, otp = otp))
        }
    }

    private fun getFlightDetails() {
        initialLoading.set(true)
        val token = sharedPrefsHelper[ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(ApiCallingKey.GetFlightDetails.name) {
            apiService.getFlightDetails(
                flightSearch.searchId,
                flightSearch.sessionId,
                flightSearch.sequence
            )
        }
    }

    private fun loadInit() {
        fetchRule()
        fetchPaymentGateWay()
        getBaggageInformation()
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

    private fun getBaggageInformation() {
        isBaggageDataLoading.set(true)
        executeSuspendedCodeBlock(ApiCallingKey.BaggageInformation.name) {
            DataManager.flightApiService.getBaggageDetails(
                flightSearch.searchId,
                flightSearch.sessionId,
                flightSearch.sequence
            )
        }
    }

    private fun setTotalBaggageCharge(baggageCharge: Double) {
        totalBaggageCharge.set(ceil(baggageCharge).toInt())
        calculateTotalPrice()
    }

    private fun prepareLuggageData(baggageResponseObj: LuggageResponse) {
        isLuggageOptional = baggageResponseObj.isLuggageOptional

        if (baggageResponseObj.wholeFlight && !baggageResponseObj.isPerPerson) {
            baggageType = BaggageType.WholeFlight
        } else if (!baggageResponseObj.wholeFlight && !baggageResponseObj.isPerPerson) {
            baggageResponseObj.routeOptions?.forEach {
                it.selectedCode = ""
            }

            baggageType = BaggageType.OnlyRouteWise

        } else if (baggageResponseObj.wholeFlight && baggageResponseObj.isPerPerson) {
            baggageType = BaggageType.OnlyPassengerWise
            var count = 0
            val travellerList = ArrayList<TravellerBaggage>()
            for (j in 1..flightSearch.adult) {
                count++
                val optionList =
                    baggageResponseObj.wholeFlightOptions?.filter { it.travellerType == TravellerType.ADT.toString() }
                val travellerBaggage = TravellerBaggage(
                    "Traveller $count (Adult)",
                    optionList!!,
                    TravellerType.ADT,
                    baggageResponseObj.isLuggageOptional
                )

                travellerList.add(travellerBaggage)
            }

            for (k in 1..flightSearch.child) {
                count++
                val optionList =
                    baggageResponseObj.wholeFlightOptions?.filter { it.travellerType == TravellerType.CHD.toString() }
                val travellerBaggage = TravellerBaggage(
                    "Traveller $count (Child)",
                    optionList!!,
                    TravellerType.CHD,
                    baggageResponseObj.isLuggageOptional
                )
                travellerList.add(travellerBaggage)
            }

            for (l in 1..flightSearch.infant) {
                count++
                val optionList =
                    baggageResponseObj.wholeFlightOptions?.filter { it.travellerType == TravellerType.INF.toString() }
                val travellerBaggage = TravellerBaggage(
                    "Traveller $count (Infant)",
                    optionList!!,
                    TravellerType.INF,
                    baggageResponseObj.isLuggageOptional
                )
                travellerList.add(travellerBaggage)
            }

            baggageResponseObj.travellerBaggageList = travellerList

        } else if (!baggageResponseObj.wholeFlight && baggageResponseObj.isPerPerson) {

            baggageType = BaggageType.RouteAndPassengerWise
            baggageResponseObj.routeOptions?.forEach { routeOptions ->
                var count = 0
                val travellerList = ArrayList<TravellerBaggage>()

                for (i in 1..flightSearch.adult) {
                    count++
                    val optionList =
                        routeOptions.options.filter { it.travellerType == TravellerType.ADT.toString() }
                    val travellerBaggage = TravellerBaggage(
                        "Traveller $count (Adult)",
                        optionList,
                        TravellerType.ADT,
                        baggageResponseObj.isLuggageOptional
                    )
                    travellerList.add(travellerBaggage)
                }

                for (i in 1..flightSearch.child) {
                    count++
                    val optionList =
                        routeOptions.options.filter { it.travellerType == TravellerType.CHD.toString() }
                    val travellerBaggage = TravellerBaggage(
                        "Traveller $count (Child)",
                        optionList,
                        TravellerType.CHD,
                        baggageResponseObj.isLuggageOptional
                    )
                    travellerList.add(travellerBaggage)
                }

                for (i in 1..flightSearch.infant) {
                    count++
                    val optionList =
                        routeOptions.options.filter { it.travellerType == TravellerType.INF.toString() }
                    val travellerBaggage = TravellerBaggage(
                        "Traveller $count (Infant)",
                        optionList,
                        TravellerType.INF,
                        baggageResponseObj.isLuggageOptional
                    )
                    travellerList.add(travellerBaggage)
                }

                routeOptions.travellerBaggageList = travellerList
            }
        }

        luggageResponse.value = baggageResponseObj
    }

    private fun getFarePolicyDetails(): String {
        return if (!flightSearch.fareDetails.isNullOrEmpty()) {
            val message = flightSearch.fareDetails
            val spanned =
                message?.let { HtmlCompat.fromHtml(it, FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH) }
            spanned.toString()
        } else
            return "No fare policy found."
    }

    private fun fetchRule() {
        executeSuspendedCodeBlock(ApiCallingKey.Rule.name) {
            DataManager.flightApiService.getAirFareRules(
                flightSearch.searchId,
                flights.sequence
            )
        }
    }

    private fun onCouponRequest(couponReq: FlightCouponRequest) {
        currentCouponRequest = couponReq

        if (currentCouponRequest.coupon.isNotEmpty()) {
            val token = sharedPrefsHelper[ACCESS_TOKEN, ""]
            executeSuspendedCodeBlock(ApiCallingKey.CouponRequest.name) {
                DataManager.flightApiService.getValidateFlightCoupon(token, couponReq)
            }
        }

        isDataLoading.value = true
    }

    private fun couponSelection() {
        wannaRedeem.set(false)
        isCardSelected.set(false)
        isRedeemSelected.set(false)
        isCouponSelected.set(true)
        availCoupon.value = true
    }

    private fun checkLoginInformation() {
        isLogin.set(sharedPrefsHelper[PrefKey.IS_LOGIN, false])
        isLoginLiveData.value = isLogin.get()
    }

    private fun updateCouponDetailsUI(discount: String, discountType: String) {
        totalPriceWithoutDiscount.set(ceil(getOriginPrice() + totalBaggageCharge.get()!!).toInt())

        when (discountType) {
            CouponType.FLAT.value -> {
                if (discountModel.couponWithDiscount) {
                    discountAmount.set(ceil(flights.priceBreakdown.getDiscount()).toInt())
                } else {
                    discountAmount.set(0)
                }
                discountModel.discountAmount = discount.toDouble()
                extraDiscountAmount.set(ceil(discount.toDouble()).toInt())
                couponDiscountAmount = discount.toDouble()
                calculateTotalPrice()
            }

            CouponType.PERCENTAGE.value -> {
                val couponDiscountValue =
                    ((discount.toDouble() / 100) * flights.priceBreakdown.details.sumOf { it.baseFare.toInt() * it.numberPaxes.toInt() })
                if (discountModel.couponWithDiscount) {
                    discountAmount.set(ceil(flights.priceBreakdown.getDiscount()).toInt())
                } else {
                    discountAmount.set(0)
                }
                extraDiscountAmount.set(ceil(couponDiscountValue).toInt())
                couponDiscountAmount = couponDiscountValue
                discountModel.discountAmount = couponDiscountValue
                calculateTotalPrice()
            }
        }
    }

    private fun fetchPaymentGateWay(gateway: List<String>? = null) {
        executeSuspendedCodeBlock(ApiCallingKey.PaymentGateway.name) {
            DataManager.flightApiService.fetchPaymentGateway(
                GatewayService.Flight.name,
                flights.gatewayCurrency,
                gateway
            )
        }
    }

    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (flag) {
                if (userTripCoin >= progress) {
                    redeemCoin.set(progress)
                    extraDiscountAmount.set(progress)
                    progressForTripCoin = progress
                    calculateTotalPrice()
                    tripCoinText.value =
                        NumberFormat.getNumberInstance(Locale.US).format((userTripCoin - progress))
                    discountModel.discountAmount = extraDiscountAmount.get().toDouble()
                } else {
                    redeemCoin.set(userTripCoin)
                    extraDiscountAmount.set(userTripCoin)
                    tripCoinText.value = NumberFormat.getNumberInstance(Locale.US)
                        .format((userTripCoin - userTripCoin))
                }
            }
            flag = true
        }
    }

    fun calculateTotalPrice() {
        val disCountAmountValue = discountAmount.get() + extraDiscountAmount.get()
        val payableAmountValue =
            getOriginPrice() + advanceIncomeTax.get()!! + totalBaggageCharge.get()!!
        if (disCountAmountValue >= payableAmountValue) {
            totalPrice.set(0)
        } else {
            val finalPayableAmount =
                (getOriginPrice() + advanceIncomeTax.get()!! + totalBaggageCharge.get()!! - disCountAmountValue)

            updateFinalPayableWithConvenience(finalPayableAmount)
        }
    }

    fun setCoupon(couponName: String) {
        couponObserver.set(FlightCouponRequest().apply {
            coupon = couponName
        })
    }

    fun wholeFlightBaggage(optionList: List<OptionsItem>?) {
        var total = 0.0
        travellerBaggageCodeList.clear()

        optionList?.forEach {
            total += it.amount
            val travellerBaggageCode = TravellerBaggageCode()
            travellerBaggageCode.luggageCodeList.add(it.code)
            for (i in 1..flightSearch.numberOfTraveller) {
                travellerBaggageCode.totalBaggageCost = it.amount
                travellerBaggageCodeList.add(travellerBaggageCode)
            }
        }
        setTotalBaggageCharge(total)
    }

    fun routeBaggage(routeOptionsList: MutableList<RouteOptionsItem>) {
        var total = 0.0
        travellerBaggageCodeList.clear()
        val travellerBaggageCode = TravellerBaggageCode()
        routeOptionsList.forEach { routeOptions ->
            val amount: Double
            if (!routeOptions.selectedCode.isNullOrEmpty()) {
                val option = routeOptions.options.find { it.code == routeOptions.selectedCode }
                amount = option!!.amount
                total += amount
            }
            travellerBaggageCode.luggageCodeList.add(routeOptions.selectedCode!!)
        }
        travellerBaggageCodeList.add(travellerBaggageCode)
        setTotalBaggageCharge(total)
    }

    fun travellerBaggage(travellerBaggageList: MutableList<TravellerBaggage>) {
        var total = 0.0
        travellerBaggageCodeList.clear()
        travellerBaggageList.forEach { travellerBaggage ->
            var amount = 0.0
            if (travellerBaggage.selectedCode.isNotEmpty()) {
                val option =
                    travellerBaggage.optionList.find { it.code == travellerBaggage.selectedCode }
                amount = option!!.amount
                total += amount
            }
            val travellerBaggageCode = TravellerBaggageCode()
            travellerBaggageCode.luggageCodeList.add(travellerBaggage.selectedCode)
            travellerBaggageCode.totalBaggageCost = amount
            travellerBaggageCodeList.add(travellerBaggageCode)
        }
        setTotalBaggageCharge(total)
    }

    fun routeAndTravellerBaggage(routePosition: Int, list: MutableList<TravellerBaggage>) {
        val luggageResponse = luggageResponse.value
        luggageResponse?.routeOptions?.get(routePosition)?.travellerBaggageList = list
        var total = 0.0

        if (travellerBaggageCodeList.isEmpty()) {
            list.forEach { _ ->
                val travellerBaggageCode = TravellerBaggageCode()
                travellerBaggageCode.luggageCodeList.add("")
                travellerBaggageCode.luggageCodeList.add("")
                travellerBaggageCodeList.add(travellerBaggageCode)
            }
        }

        luggageResponse?.routeOptions?.forEachIndexed { routeIndex, routeOptions ->
            routeOptions.travellerBaggageList.forEachIndexed { travellerIndex, travellerBaggage ->
                val amount: Double
                if (travellerBaggage.selectedCode.isNotEmpty()) {
                    val option =
                        travellerBaggage.optionList.find { it.code == travellerBaggage.selectedCode }
                    amount = option!!.amount
                    total += amount
                }
                travellerBaggageCodeList[travellerIndex].luggageCodeList[routeIndex] =
                    travellerBaggage.selectedCode
            }
        }
        setTotalBaggageCharge(total)
    }

    fun getMyTripCoins() = userTripCoin

    fun onBookButtonClicked() {
        if (isCouponSelected.get() && flightCoupon.isEmpty()) {
            showMessage("Please apply a coupon or select another option")
        } else if (isCouponSelected.get() && gpStarCouponVerificationNeeded.get()) {
            showMessage("Please verify your GPSTAR number")
        } else {
            if (isBaggageDataLoading.get()) {
                showMessage("Please wait for baggage data")
            } else {
                if (travellerBaggageCodeList.isEmpty() && !isLuggageOptional) {
                    showMessage("Please select baggage data")
                } else {
                    if (travellerBaggageCodeList.isNotEmpty()) {
                        flightSearch.travellerBaggageCodes = travellerBaggageCodeList
                    } else {
                        for (i in 1..flightSearch.totalTravellers) {
                            val travellerBaggage = TravellerBaggageCode()
                            if (baggageType == BaggageType.RouteAndPassengerWise || baggageType == BaggageType.OnlyRouteWise) {
                                travellerBaggage.luggageCodeList = arrayListOf("", "")
                            } else {
                                travellerBaggage.luggageCodeList = arrayListOf("")
                            }
                            travellerBaggageCodeList.add(travellerBaggage)
                        }
                        flightSearch.travellerBaggageCodes = travellerBaggageCodeList
                    }
                    flightSearch.totalBaggageCost = totalBaggageCharge.get()!!.toDouble()
                    flightEventManager.clickOnBookNow()
                    if (sharedPrefsHelper[PrefKey.IS_LOGIN, false]) {
                        gotoCheckout.value = Event(true)
                    } else {
                        isShowDialog.value = false
                    }
                }
            }
        }
    }

    fun onFareDetailsButtonClicked() {
        flightEventManager.flightHistory()
        ruleCase.value = Event(2)
    }

    fun onBaggageButtonClicked() {
        flightEventManager.clickOnFlightBaggage()
        ruleCase.value = Event(0)
    }

    fun onAirFareRuleButtonClicked() {
        flightEventManager.clickOnAirFareRules()
        ruleCase.value = Event(1)
    }

    fun onCouponApply() {
        if (couponObserver.get()?.coupon?.isNotEmpty() == true) {
            val couponRequest = couponObserver.get()
            couponRequest?.let {
                it.deviceType = "Android"
                it.serviceType = "Flight"
                it.extraParams = FlightExtraParams(flightSearch.searchId, flights.sequence)
                onCouponRequest(it)
            }
        } else
            showMessage(PLEASE_SELECT_A_VALID_COUPON)
    }

    fun onVerifyClick() {
        if (couponState.value != GPCouponInputState.OTPInputState.name) {
            if (inputObserver.get().isPhoneNumberValid()) {
                gpLoyaltyCheck(inputObserver.get().toString())
            } else {
                showMessage(PLEASE_PROVIDE_A_VALID_PHONE_NUMBER)
            }

        } else {
            otpVerification(gpStarNumber, inputObserver.get().toString())
        }
    }

    fun onResendClick() {
        if (timerOrResendAction.get() == UtilText.otpResend) {
            gpLoyaltyCheck(gpStarNumber)
        }
    }

    private fun counter(seconds: Long = COUNTDOWN_TIME) {
        sendAgain.set(false)
        timer = object : CountDownTimer(seconds, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val min = (millisUntilFinished / 1000) / 60
                val sec = (millisUntilFinished / 1000) % 60
                timerOrResendAction.set(String.format(COUNTDOWN_FORMAT, min, sec))
            }

            override fun onFinish() {
                sendAgain.set(true)
                timerOrResendAction.set(UtilText.otpResend)
            }
        }
        timer.start()
    }

    fun useAnotherNumber() {
        gpCouponInputHint.set(enterPhone)
        couponState.value = GPCouponInputState.MobileInputState.name
        if (::timer.isInitialized) timer.cancel()
        timerOrResendAction.set("")
        inputObserver.set("")
    }

    fun onClickCheckBox(view: View) {
        if (view is RadioButton) {
            when (view.id) {
                R.id.redeem_check_box -> {
                    flightEventManager.clickOnRedeemTripCoinInFlight()
                    isCardSelected.set(false)
                    isRedeemSelected.set(true)
                    isCouponSelected.set(false)
                    wannaRedeem.set(true)
                    redeemChecked.value = true
                }

                R.id.radio_button_card_payment -> {
                    onEarnTripCoinChecked()
                }

                R.id.radio_button_coupon -> {
                    couponSelection()
                }
            }
        }
    }

    fun onEarnTripCoinChecked() {
        isCardSelected.set(true)
        isRedeemSelected.set(false)
        isCouponSelected.set(false)
        cardPaymentChecked.value = true
        wannaRedeem.set(false)
    }

    fun onCouponChecked() {
        if (discountModel.couponWithDiscount) {
            discountAmount.set(ceil(flights.priceBreakdown.getDiscount()).toInt())
        } else {
            discountAmount.set(0)
        }
        extraDiscountAmount.set(ceil(couponDiscountAmount).toInt())
        extraDiscountLabel.set("Redeem Coupon")
        isExtraDiscountVisible.set(true)
        calculateTotalPrice()
        isRedeemCodeActivated = false
        tripCoinText.value = NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
        discountModel.type = DiscountType.COUPON.value
        discountModel.discountAmount = couponDiscountAmount
    }

    fun onRedeemChecked() {
        extraDiscountAmount.set(ceil(redeemCoin.get().toDouble()).toInt())
        discountAmount.set(0)
        isExtraDiscountVisible.set(true)
        extraDiscountLabel.set("Redeem Coins")
        calculateTotalPrice()
        isRedeemCodeActivated = true
        tripCoinText.value =
            NumberFormat.getNumberInstance(Locale.US).format((userTripCoin - progressForTripCoin))
        discountModel.type = DiscountType.COIN.value
    }

    fun onCardChecked() {
        extraDiscountAmount.set(0)
        discountAmount.set(ceil(flights.priceBreakdown.getDiscount()).toInt())
        isExtraDiscountVisible.set(false)
        calculateTotalPrice()
        isRedeemCodeActivated = false
        tripCoinText.value = NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
        discountModel.type = DiscountType.CARD.value
        discountModel.discountAmount = flights.priceBreakdown.getDiscount()
    }

    private fun getOriginPrice() = flights.priceBreakdown.originPrice

    fun gotoSegmentFragment(scrollToPosition: Int) {
        var position = scrollToPosition
        if (position > 0) {
            position = flights.segments[0].segment.size + 1
        }
        gotoSegment.value = Event(position)
    }

    fun onLoginSuccess() {
        isLogin.set(true)
        val token = sharedPrefsHelper[ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(ApiCallingKey.FetchUserInfo.name) {
            apiService.getUserInformation(token)
        }
    }

    fun onClickBaggageExpanded() {
        isBaggageExpanded.set(!isBaggageExpanded.get())
    }

    override fun onClickLogin() {
        isLoginClicked.value = Event(true)
    }

}
