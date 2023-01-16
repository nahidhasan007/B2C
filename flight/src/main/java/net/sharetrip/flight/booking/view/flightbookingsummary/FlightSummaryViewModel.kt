package net.sharetrip.flight.booking.view.flightbookingsummary

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.PrefKey.BOOKING_DATE
import com.sharetrip.base.data.PrefKey.RETURN_DATE
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
import net.sharetrip.shared.utils.UtilText.couponSubTitle
import net.sharetrip.shared.utils.UtilText.couponTitle
import net.sharetrip.shared.utils.UtilText.enterOtp
import net.sharetrip.shared.utils.UtilText.enterPhone
import net.sharetrip.shared.utils.UtilText.otpResend
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.*
import net.sharetrip.flight.booking.model.FlightRevalidateStatus
import net.sharetrip.flight.booking.model.coupon.*
import net.sharetrip.flight.booking.model.flightresponse.DiscountModel
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown
import net.sharetrip.flight.booking.model.payment.COUPON
import net.sharetrip.flight.booking.model.payment.EARN
import net.sharetrip.flight.booking.model.payment.REDEEM
import net.sharetrip.flight.booking.model.payment.RemoteDiscountModel
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.CLICK_TO_AGREE
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.COUPON_ADDED_SUCCESSFULLY
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.SOMETHING_WENT_WRONG
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.network.PaymentGatewayType
import net.sharetrip.flight.utils.PriceBreakDownUtil
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.shared.utils.*
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import kotlin.math.ceil

class FlightSummaryViewModel(
    private val flightsInfo: Flights,
    private val flightSearch: FlightSearch,
    val discountModel: DiscountModel,
    private val couponResponse: CouponResponse,
    private val itemTravellers: List<ItemTraveler>,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private val convenienceVatFlag = false
    private val flightEventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private var flag: Boolean = false
    private var isRedeemCodeActivated: Boolean = false
    private var priceBreakDownData: PriceBreakdown? = null
    private var userTripCoin: Int = 0
    private lateinit var timer: CountDownTimer
    private lateinit var couponRequest: FlightCouponRequest

    val isCheckboxActive = ObservableBoolean(false)
    val paymentMethodList = MutableLiveData<List<PaymentMethod>>()
    val clickedBooking = MutableLiveData<Boolean>()
    val redeemChecked = MutableLiveData<Boolean>()
    val availCoupon = MutableLiveData<Boolean>()
    val isProceedToPayment = MutableLiveData<Boolean>()
    val cardPaymentChecked = MutableLiveData<Boolean>()
    val showMessageWithDialog = MutableLiveData<String>()
    val flightInfoLiveData = MutableLiveData<ArrayList<Any>>()
    val searchFlightAgain = MutableLiveData<String>()

    val usdPayment = MutableLiveData<Double>()
    val tripCoinText = MutableLiveData<Event<String>>()
    val isDomestic = ObservableBoolean()
    val isCardSelected = ObservableBoolean(false)
    val isRedeemSelected = ObservableBoolean(false)
    val isCouponSelected = ObservableBoolean(false)
    val isCouponShow = ObservableBoolean(true)
    val isFlightSummaryExpand = ObservableBoolean(false)
    val isDiscountOptionExpand = ObservableBoolean(false)
    val wannaRedeem = ObservableBoolean()
    val redeemCoin = ObservableInt()
    val progressBar = ObservableBoolean(false)
    val couponObserver = ObservableField<FlightCouponRequest>()
    val flightInfoObservable = ObservableField<String>()
    val flightDateObservable = ObservableField<String>()
    val discountName = ObservableField<String>()
    val currency = ObservableField<String>()
    val priceBreakdownMsg = ObservableField<CharSequence>()

    val payButtonText = ObservableField("Pay Now")
    var bankDiscountInfo = ""

    var flightCoupon = ""
    var progressForTripCoin = 0
    var conversionRate = 0.0
    var couponDiscountAmount = 0.0
    var totalBaggageChargeBDT = 0.0
    var advanceIncomeTaxBDT = 0.0
    var bookingDate: Long? = null
    var returnBookingDate: Long? = null
    var isExtraDiscountVisible = ObservableBoolean(false)
    var extraDiscountLabel = ObservableField<String>()
    val extraDiscountAmount = ObservableInt()
    val isConvenienceVisible = ObservableBoolean(false)
    val selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    val totalConvenienceCharge = ObservableField(0)
    val vatCharge = ObservableField(0)
    val totalPrice = ObservableInt()
    val totalPriceWithoutDiscount = ObservableInt()
    val discountAmount = ObservableInt()
    var discountAmountBDT = 0
    var covidAmount = ObservableInt(0)
    var covidAmountBDT = 0
    var travelInsuranceAmount = ObservableInt(0)
    var travelInsuranceAmountBDT = 0
    var baggageInsuranceAmount = ObservableInt(0)
    var baggageInsuranceAmountBDT = 0
    var priceCheckRes = MutableLiveData<Pair<Int, Int>>()
    val totalBaggageCharge = ObservableInt(0)
    val advanceIncomeTax = ObservableInt(0)
    val paymentOptionMsg: PaymentOptionMsg

    val couponState = MutableLiveData(GPCouponInputState.MobileInputState.name)
    val sendAgain = ObservableBoolean(false)
    val gpStarCouponVerificationNeeded = ObservableBoolean(false)
    var gpCouponTitle = ObservableField(couponTitle)
    var gpCouponSubTitle = ObservableField(couponSubTitle)
    var timerOrResendAction = ObservableField("")
    var gpCouponInputHint = ObservableField(enterPhone)
    var inputObserver = ObservableField<String>()
    var gpStarNumber = MutableLiveData<String>()
    var verifiedOTP = MutableLiveData<String>()

    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {}

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (flag) {
                if (userTripCoin >= progress) {
                    redeemCoin.set(progress)

                    if (currency.get() == PaymentGatewayType.USD.name) {
                        extraDiscountAmount.set(ceil(progress / conversionRate).toInt())
                        calculateTotalPrice()
                        return
                    }

                    extraDiscountAmount.set(ceil(progress.toDouble()).toInt())
                    discountAmountBDT = 0
                    progressForTripCoin = progress
                    calculateTotalPrice()
                    tripCoinText.value = Event(
                        NumberFormat.getNumberInstance(Locale.US).format((userTripCoin - progress))
                    )
                } else {
                    redeemCoin.set(userTripCoin)
                    tripCoinText.value = Event(
                        NumberFormat.getNumberInstance(Locale.US)
                            .format((userTripCoin - progress))
                    )
                }
            }
            flag = true
        }
    }

    init {
        flightEventManager.initialCheckoutFlight()
        userTripCoin = sharedPrefsHelper[USER_TRIP_COIN, "0"].filter { it in '0'..'9' }.toInt()

        val listType = object : TypeToken<List<RemoteDiscountModel>>() {}.type
        val flightDiscountModel = Gson().fromJson<List<RemoteDiscountModel>>(
            sharedPrefsHelper[PrefKey.FLIGHT_DISCOUNT_OPTIONS, PrefKey.DEFAULT_FLIGHT_DISCOUNT_OPTION],
            listType
        )

        totalBaggageCharge.set(ceil(flightSearch.totalBaggageCost).toInt())
        totalBaggageChargeBDT = flightSearch.totalBaggageCost

        paymentOptionMsg = PaymentOptionMsg(
            flightDiscountModel.find { it.type == EARN }!!.title,
            flightDiscountModel.find { it.type == REDEEM }!!.title,
            flightDiscountModel.find { it.type == COUPON }!!.title
        )

        bankDiscountInfo = sharedPrefsHelper[PrefKey.FLIGHT_DISCOUNT_OFFER_BANK_LIST, ""]
        wannaRedeem.set(false)
        couponObserver.set(FlightCouponRequest())

        if (flightsInfo.availableCoupons.isNullOrEmpty()) {
            isCouponShow.set(false)
        }

        setAdvanceIncomeTax()
        setCovidPrice()
        setTravelInsurancePrice()
        setBaggageInsurancePrice()
        setFlightInfo()
        setFlightDate()
        initDetailsAdapter()
        fetchPaymentGateWay()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            ApiCallingKey.CouponRequest.name -> {
                progressBar.set(false)
                val couponDataResponse = (data.body as RestResponse<*>).response as CouponResponse
                couponDataResponse.withDiscount.let {
                    discountModel.couponWithDiscount = it == "Yes"
                }
                discountModel.gateway = couponDataResponse.gateway
                paymentMethodList.postValue(paymentMethodList.value)
                flightCoupon = couponRequest.coupon

                updateCouponDetailsUI(
                    couponDataResponse.discount,
                    couponDataResponse.discountType
                )

                showMessage(COUPON_ADDED_SUCCESSFULLY)

                gpStarCouponVerificationNeeded.set(
                    couponDataResponse.mobileVerificationRequired?.lowercase().equals("yes")
                )

                if (gpStarCouponVerificationNeeded.get()) {
                    showMessage(UtilText.gpNumberVerify)
                }
            }
            ApiCallingKey.FetchPaymentGateWay.name -> {
                paymentMethodList.value =
                    (data.body as RestResponse<*>).response as List<PaymentMethod>
                dataLoading.set(false)
                paymentMethodSelection()
            }
            ApiCallingKey.FetchPaymentUrl.name -> {
                progressBar.set(false)
                val flightList = flightsInfo.flight
                when (flightList.size) {
                    1 -> {
                        try {
                            bookingDate =
                                DateUtil.parseDateTimeToMillisecond(
                                    flightList[0].departureDateTime.date,
                                    flightList[0].departureDateTime.time,
                                    dateFormat = DateFormatPattern.API_DATE_TIME_PATTERN.datePattern
                                )
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    }
                    2 -> {
                        try {
                            bookingDate =
                                DateUtil.parseDateTimeToMillisecond(
                                    flightList[0].departureDateTime.date,
                                    flightList[0].departureDateTime.time,
                                    dateFormat = DateFormatPattern.API_DATE_TIME_PATTERN.datePattern
                                )
                            returnBookingDate =
                                DateUtil.parseDateTimeToMillisecond(
                                    flightList[flightList.size - 1].departureDateTime.date,
                                    flightList[flightList.size - 1].departureDateTime.time,
                                    dateFormat = DateFormatPattern.API_DATE_TIME_PATTERN.datePattern
                                )
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    }
                }

                val bundle = Bundle()
                bundle.putString(ARG_PAYMENT_URL, data.body.toString())
                bundle.putString(SERVICE_TYPE, SERVICE_TYPE_FLIGHT)
                navigateWithArgument(GOTO_PAYMENT, bundle)

                sharedPrefsHelper.put(BOOKING_DATE, bookingDate ?: 0)
                sharedPrefsHelper.put(RETURN_DATE, returnBookingDate ?: 0)
            }
            ApiCallingKey.CheckPriceBeforeBooking.name -> {
                val response: RestResponse<*> = data.body as RestResponse<*>
                if (response.code == FlightRevalidateStatus.PRICE_CHANGE.value ||
                    response.code == FlightRevalidateStatus.ITINERARY_CHANGE.value
                ) {
                    when {
                        isCouponSelected.get() -> {
                            val priceCheckResponse = response.response as PriceCheckResponse
                            priceCheckRes.value = Pair(
                                totalPrice.get(),
                                couponUpdateAfterReValidation(priceCheckResponse)
                            )
                        }
                        isRedeemSelected.get() -> {
                            priceCheckRes.value = Pair(
                                totalPrice.get(),
                                ((response.response as PriceCheckResponse).priceBreakdown!!.originPrice - redeemCoin.get()).toInt()
                            )
                        }
                        else -> {
                            priceCheckRes.value = Pair(
                                totalPrice.get(),
                                ((response.response as PriceCheckResponse).priceBreakdown!!.promotionalDiscount).toInt()
                            )
                        }
                    }
                } else if (response.code == FlightRevalidateStatus.RE_ITINERARY_CHANGE.value ||
                    response.code == FlightRevalidateStatus.RE_VALIDATION_CHANGE.value
                ) {
                    if (response.message.isNotEmpty())
                        searchFlightAgain.value = response.message
                } else {
                    isProceedToPayment.value = true
                }
            }
            ApiCallingKey.GpLoyaltyCheck.name -> {
                progressBar.set(false)
                flightSearch.verifiedMobileNumber = gpStarNumber.value.toString()
                val response = (data.body as RestResponse<*>).response as GPLoyaltyCheckResponse
                if (response.success == true) {
                    gpCouponInputHint.set(enterOtp)
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
                flightSearch.otp = verifiedOTP.value.toString()
                timer.cancel()
                timerOrResendAction.set("")
                inputObserver.set("")
                couponState.value = GPCouponInputState.MobileInputState.name
                gpCouponInputHint.set(enterPhone)
                gpStarCouponVerificationNeeded.set(false)
                showMessage((data.body as RestResponse<*>).message)
                progressBar.set(false)
            }
        }
    }

    private fun gpLoyaltyCheck(phoneNo: String) {
        progressBar.set(true)
        gpStarNumber.value = phoneNo
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(ApiCallingKey.GpLoyaltyCheck.name) {
            DataManager.flightApiService.gpLoyaltyCheck(
                token,
                GpLoyaltyCheckRequest(mobileNumber = phoneNo)
            )
        }
    }

    private fun otpVerification(phoneNo: String, otp: String) {
        progressBar.set(true)
        verifiedOTP.value = otp
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(ApiCallingKey.OTPVerify.name) {
            DataManager.flightApiService.verifyOTP(
                token,
                VerifyOTPRequest(mobileNumber = phoneNo, otp = otp)
            )
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            ApiCallingKey.CouponRequest.name -> {
                progressBar.set(false)
                if (errorMessage.isNotEmpty() && errorMessage.length > 4)
                    showMessage(errorMessage)
                else
                    showMessage(SOMETHING_WENT_WRONG)
            }

            ApiCallingKey.CouponList.name -> {
                progressBar.set(false)
                if (errorMessage.isNotEmpty() && errorMessage.length > 4)
                    showMessage(errorMessage)
                else
                    showMessage(SOMETHING_WENT_WRONG)
            }

            ApiCallingKey.FetchPaymentGateWay.name -> {
                dataLoading.set(false)
                showMessage(errorMessage)
            }

            ApiCallingKey.FetchPaymentUrl.name -> {
                progressBar.set(false)
                if (errorMessage.isNotEmpty() && errorMessage.length > 4)
                    showMessage(errorMessage)
                else
                    showMessage(SOMETHING_WENT_WRONG)
            }

            ApiCallingKey.CheckPriceBeforeBooking.name -> {
                progressBar.set(false)
                showMessageWithDialog.value = errorMessage
            }

            ApiCallingKey.GpLoyaltyCheck.name -> {
                showMessage(errorMessage)
                progressBar.set(false)
            }

            ApiCallingKey.OTPVerify.name -> {
                showMessage(errorMessage)
                progressBar.set(false)
            }
        }
    }

    private fun setFlightInfo() {
        val destination = flightSearch.destination[flightSearch.destination.size - 1]
        val origin = flightSearch.origin[0]
        isDomestic.set(flightsInfo.domestic)
        flightInfoObservable.set("$origin - $destination")
    }

    private fun setCovidPrice() {
        itemTravellers.forEach {
            if (it.covidTestOption != null) {
                var amount = covidAmount.get()
                amount += ceil(it.covidTestOption!!.discountPrice).toInt()
                covidAmount.set(amount)
                covidAmountBDT = amount
            }
        }
    }

    private fun setTravelInsurancePrice() {
        itemTravellers.forEach {
            if (it.travelInsuranceOption != null) {
                var amount = travelInsuranceAmount.get()
                val price = ceil(it.travelInsuranceOption!!.discountPrice).toInt()
                val segmentsSize = flightsInfo.segments.size
                amount += price * segmentsSize
                travelInsuranceAmount.set(amount)
                travelInsuranceAmountBDT = amount
            }
        }
    }

    private fun setBaggageInsurancePrice() {
        itemTravellers.forEach {
            if (it.passengerType != PassengerType.INFANT)
                if (it.baggageInsuranceOption != null) {
                    var amount = baggageInsuranceAmount.get()
                    val price = ceil(it.baggageInsuranceOption!!.discountPrice).toInt()
                    amount += price
                    baggageInsuranceAmount.set(amount)
                    baggageInsuranceAmountBDT = amount
                }
        }
    }

    private fun setAdvanceIncomeTax() {
        advanceIncomeTax.set(ceil(flightsInfo.priceBreakdown.advanceIncomeTax).toInt())
        advanceIncomeTaxBDT = flightsInfo.priceBreakdown.advanceIncomeTax
    }

    private fun initDetailsAdapter() {
        val mList = ArrayList<Any>()
        val mFlight = flightsInfo.flight
        val mItemSegments = flightsInfo.segments
        if (mFlight.size == mItemSegments.size) {
            val mCount = mFlight.size
            for (i in 0 until mCount) {
                val flight = mFlight[i]
                mList.add(flight)
            }
            flightInfoLiveData.value = mList
        }
    }

    private fun setFlightDate() {
        val mBuilder = StringBuilder()
        val depart = flightSearch.depart
        if (depart.size == 1) {
            try {
                val mDate = DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[0])
                mBuilder.append(mDate)
                    .append(" " + flightSearch.numberOfTraveller.toString() + " Traveller(s)")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            try {
                val mStartDate = DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[0])
                val mEndDate =
                    DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[depart.size - 1])
                mBuilder.append(mStartDate).append(" - ").append(mEndDate)
                    .append(" " + flightSearch.numberOfTraveller.toString() + " Traveller(s)")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        flightDateObservable.set(mBuilder.toString())
    }

    private fun onCouponRequest(couponReq: FlightCouponRequest) {
        progressBar.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        couponRequest = couponReq
        executeSuspendedCodeBlock(ApiCallingKey.CouponRequest.name) {
            DataManager.flightApiService.getValidateFlightCoupon(token, couponReq)
        }
    }

    private fun fetchPaymentGateWay(gateway: List<String>? = null) {
        dataLoading.set(true)
        executeSuspendedCodeBlock(ApiCallingKey.FetchPaymentGateWay.name) {
            DataManager.flightApiService.fetchPaymentGateway(
                GatewayService.Flight.name,
                flightsInfo.gatewayCurrency,
                gateway
            )
        }
    }

    private fun paymentMethodSelection() {
        when (discountModel.type) {
            "Coupon" -> {
                couponDiscountAmount = discountModel.discountAmount
                extraDiscountAmount.set(ceil(couponDiscountAmount).toInt())
                flightCoupon = discountModel.coupon
                val couponRequest = FlightCouponRequest()
                couponRequest.let {
                    it.deviceType = "Android"
                    it.serviceType = "Flight"
                    it.coupon = discountModel.coupon
                    couponObserver.set(couponRequest)
                }
                availCoupon.value = true
            }
            "Coin" -> {
                redeemCoin.set(discountModel.discountAmount.toInt())
                progressForTripCoin = discountModel.discountAmount.toInt()
                redeemChecked.value = true
            }
            else ->
                cardPaymentChecked.value = true
        }
    }

    private fun couponUpdateAfterReValidation(response: PriceCheckResponse): Int {
        if (response.priceBreakdown == null)
            return 0
        return when (couponResponse.discountType) {
            "Flat" -> response.priceBreakdown.originPrice.minus(couponResponse.discount.toInt())

            "Percentage" -> {
                val couponDiscount = ((couponResponse.discount.toDouble() / 100) *
                        response.priceBreakdown.details.sumOf { it.baseFare.toInt() * it.numberPaxes.toInt() })
                response.priceBreakdown.originPrice - couponDiscount.toInt()
            }

            else -> response.priceBreakdown.originPrice
        }.toInt()
    }

    private fun updateCouponDetailsUI(discount: String, discountType: String) {
        when (discountType) {
            "Flat" -> {
                discountAmountBDT = if (discountModel.couponWithDiscount) {
                    discountAmount.set(ceil(flightsInfo.priceBreakdown.getDiscount()).toInt())
                    ceil(flightsInfo.priceBreakdown.getDiscount()).toInt()
                } else {
                    discountAmount.set(0)
                    0
                }
                couponDiscountAmount = discount.toDouble()
            }

            "Percentage" -> {
                val couponDiscountValue =
                    ((discount.toDouble() / 100) * flightsInfo.priceBreakdown.details.sumOf { it.baseFare.toInt() * it.numberPaxes.toInt() })

                discountAmountBDT = if (discountModel.couponWithDiscount) {
                    discountAmount.set(ceil(flightsInfo.priceBreakdown.getDiscount()).toInt())
                    ceil(flightsInfo.priceBreakdown.getDiscount()).toInt()
                } else {
                    discountAmount.set(0)
                    0
                }
                couponDiscountAmount = couponDiscountValue
            }
        }

        if (currency.get() == PaymentGatewayType.USD.toString()) {
            extraDiscountAmount.set(ceil(couponDiscountAmount / conversionRate).toInt())
        } else {
            extraDiscountAmount.set(ceil(couponDiscountAmount).toInt())
        }

        calculateTotalPrice()
    }

    private fun getOriginPrice() = flightsInfo.priceBreakdown.originPrice

    private fun getOriginPriceForUSD() = priceBreakDownData!!.originPrice.twoDigitDecimal()

    private fun calculateTotalPrice() {
        val disCountAmountValue = discountAmount.get() + extraDiscountAmount.get()
        val payableAmountValue =
            getOriginPrice() + advanceIncomeTax.get() + totalBaggageCharge.get()
        if (disCountAmountValue >= payableAmountValue) {
            totalPriceWithoutDiscount.set(ceil(getOriginPrice()).toInt())
            totalPrice.set(0)
        } else {
            val finalPayableAmount = if (currency.get() == PaymentGatewayType.USD.toString()) {
                totalPriceWithoutDiscount.set(ceil(getOriginPriceForUSD()).toInt())
                (getOriginPriceForUSD() + advanceIncomeTax.get() + totalBaggageCharge.get() + covidAmount.get() + travelInsuranceAmount.get() + baggageInsuranceAmount.get()
                        - discountAmount.get() - extraDiscountAmount.get())
            } else {
                totalPriceWithoutDiscount.set(ceil(getOriginPrice()).toInt())
                (getOriginPrice() + advanceIncomeTax.get() + totalBaggageCharge.get() + covidAmount.get() + travelInsuranceAmount.get() + baggageInsuranceAmount.get()
                        - discountAmount.get() - extraDiscountAmount.get())
            }

            updateFinalPayableWithConvenience(finalPayableAmount)
        }
        setPayButtonText()
    }

    private fun updateFinalPayableWithConvenience(payableAmount: Double) {
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
                convenienceData.third
            }

        totalPrice.set(ceil(finalPayableAmount).toInt())
    }

    private fun calculateConvenience(totalAmount: Double): Triple<Double, Double, Double> {
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
            payButtonText.set("Book Now")
        } else {
            payButtonText.set("Pay Now")
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
                timerOrResendAction.set(otpResend)
            }
        }
        timer.start()
    }

    fun onCouponApply() {
        val couponRequest = couponObserver.get()
        couponRequest?.let {
            it.deviceType = "Android"
            it.serviceType = "Flight"
            it.extraParams = FlightExtraParams(flightSearch.searchId, flightSearch.sequence)
            onCouponRequest(it)
        }
    }

    fun onVerifyClick() {
        if (couponState.value != GPCouponInputState.OTPInputState.name) {
            if (inputObserver.get().isPhoneNumberValid()) {
                gpLoyaltyCheck(inputObserver.get().toString())
            } else {
                showMessage(UIMessageData.PLEASE_PROVIDE_A_VALID_PHONE_NUMBER)
            }

        } else {
            otpVerification(gpStarNumber.value.toString(), inputObserver.get().toString())
        }
    }

    fun onResendClick() {
        if (timerOrResendAction.get() == otpResend) {
            gpLoyaltyCheck(gpStarNumber.value.toString())
        }
    }

    fun useAnotherNumber() {
        gpCouponInputHint.set(enterPhone)
        couponState.value = GPCouponInputState.MobileInputState.name
        if (::timer.isInitialized) timer.cancel()
        timerOrResendAction.set("")
        inputObserver.set("")
    }

    fun noCardSeriesAvailable() {
        isCouponShow.set(false)
    }

    fun onClickCheckBox(view: View) {
        if (view is RadioButton) {

            when (view.id) {

                R.id.redeem_check_box ->
                    redeemChecked.value = true

                R.id.radio_button_card_payment ->
                    cardPaymentChecked.value = true

                R.id.radio_button_coupon ->
                    availCoupon.value = true
            }
        }
    }

    fun onClickBookNow() {
        if (isCouponSelected.get() && flightCoupon.isEmpty()) {
            showMessage("Please apply a coupon or select another option")
        } else if (isCouponSelected.get() && gpStarCouponVerificationNeeded.get()) {
            showMessage("Please verify your GPSTAR number")
        } else {
            if (isCheckboxActive.get()) {
                clickedBooking.value = true
            } else {
                showMessage(CLICK_TO_AGREE)
            }
        }
    }

    fun fetchPaymentUrl(bookingDetail: FlightBookingDetail) {
        progressBar.set(true)
        bookingDetail.tripCoin = if (isRedeemCodeActivated) redeemCoin.get() else 0
        bookingDetail.passengers.adult[0].mobileNumber =
            bookingDetail.passengers.adult[0].mobileNumber?.replace("+", "00")
        bookingDetail.coupon = if (isCouponSelected.get()) flightCoupon else ""

        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock(ApiCallingKey.FetchPaymentUrl.name) {
            DataManager.flightApiService.fetchFlightPaymentUrl(token, bookingDetail)
        }
    }

    fun getMyTripCoins() = userTripCoin

    fun onCouponChecked() {
        discountModel.type = "Coupon"
        discountAmountBDT = if (discountModel.couponWithDiscount) {
            discountAmount.set(ceil(flightsInfo.priceBreakdown.getDiscount()).toInt())
            ceil(flightsInfo.priceBreakdown.getDiscount()).toInt()
        } else {
            discountAmount.set(0)
            0
        }

        isExtraDiscountVisible.set(true)
        extraDiscountLabel.set("Redeem Coupon")
        extraDiscountAmount.set(ceil(couponDiscountAmount).toInt())
        calculateTotalPrice()
        discountName.set(paymentOptionMsg.couponMsg)
        wannaRedeem.set(false)
        isCardSelected.set(false)
        isRedeemSelected.set(false)
        isCouponSelected.set(true)
        isRedeemCodeActivated = false
        tripCoinText.value = Event(NumberFormat.getNumberInstance(Locale.US).format((userTripCoin)))
    }

    fun onRedeemChecked() {
        discountModel.type = "Coin"
        discountName.set(paymentOptionMsg.redeemMsg)
        isCardSelected.set(false)
        isRedeemSelected.set(true)
        isCouponSelected.set(false)
        discountAmount.set(0)
        extraDiscountAmount.set(ceil(redeemCoin.get().toDouble()).toInt())
        extraDiscountLabel.set("Redeem Coins")
        isExtraDiscountVisible.set(true)
        discountAmountBDT = 0
        calculateTotalPrice()
        isRedeemCodeActivated = true
        tripCoinText.value =
            Event(
                NumberFormat.getNumberInstance(Locale.US)
                    .format((userTripCoin - progressForTripCoin))
            )
    }

    fun onCardChecked() {
        discountModel.type = "Card"
        discountName.set(paymentOptionMsg.earnMsg)
        isCardSelected.set(true)
        isRedeemSelected.set(false)
        isCouponSelected.set(false)
        discountAmount.set(ceil(flightsInfo.priceBreakdown.getDiscount()).toInt())
        discountAmountBDT = ceil(flightsInfo.priceBreakdown.getDiscount()).toInt()
        extraDiscountAmount.set(0)
        isExtraDiscountVisible.set(false)
        calculateTotalPrice()
        isRedeemCodeActivated = false
        tripCoinText.value = Event(NumberFormat.getNumberInstance(Locale.US).format((userTripCoin)))
    }

    fun onSummaryLayoutClick() {
        isFlightSummaryExpand.set(!isFlightSummaryExpand.get())
    }

    fun onChangeBtnClick() {
        isDiscountOptionExpand.set(!isDiscountOptionExpand.get())
    }

    fun checkPriceBeforeBooking() {
        progressBar.set(true)

        val priceCheck =
            PriceCheckBody(flightsInfo.sequence, flightSearch.searchId, flightSearch.sessionId)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock(ApiCallingKey.CheckPriceBeforeBooking.name) {
            DataManager.flightApiService.priceCheckBeforeFlightBooking(token, priceCheck)
        }
    }

    fun goToFlightDashboard() {
        navigateWithArgument(GOTO_FLIGHT_DASHBOARD, "")
    }

    fun onClickTermsAndConditionCheckbox() {
        isCheckboxActive.set(!isCheckboxActive.get())
    }

    fun onClickPrivacyPolicy() {
        navigateWithArgument(GOTO_PRIVACY, "")
    }

    fun onClickTermsAndCondition() {
        navigateWithArgument(GOTO_TERMS, "")
    }

    fun setCoupon(couponName: String) {
        couponObserver.set(FlightCouponRequest().apply {
            coupon = couponName
        })
    }

    fun updateUSDPayment(paymentMethod: PaymentMethod) {
        if (priceBreakDownData == null) {
            conversionRate = paymentMethod.currency.conversion.rate
            priceBreakDownData = PriceBreakdown(details = ArrayList())

            priceBreakDownData?.apply {
                subTotal = flightsInfo.priceBreakdown.subTotal
                couponAmount = flightsInfo.priceBreakdown.couponAmount
                discountAmount = flightsInfo.priceBreakdown.discountAmount
                total = flightsInfo.priceBreakdown.total

                originPrice = getOriginPrice() / conversionRate
                promotionalAmount = flightsInfo.priceBreakdown.promotionalAmount / conversionRate

                promotionalDiscount = flightsInfo.priceBreakdown.promotionalDiscount
                currency = flightsInfo.priceBreakdown.currency
            }

            flightsInfo.priceBreakdown.details.forEach {
                (priceBreakDownData?.details as ArrayList).add(it.copy())
            }

            priceBreakDownData?.details?.forEachIndexed { index, priceDetail ->
                val base = (priceDetail.baseFare.toDouble() / conversionRate).toString()
                priceBreakDownData!!.details[index].baseFare = base
                priceBreakDownData!!.details[index].tax =
                    (priceDetail.tax.toDouble() / conversionRate).toString()
            }
        }

        currency.set(PaymentGatewayType.USD.toString())
        val priceString = PriceBreakDownUtil.getFormattedPriceBreakDown(priceBreakDownData!!)
        priceBreakdownMsg.set(priceString)
        currency.notifyChange()
        priceBreakdownMsg.notifyChange()

        if (isCouponSelected.get()) {
            if (discountModel.couponWithDiscount) {
                val charge = priceBreakDownData!!.getDiscount() / conversionRate
                discountAmount.set(ceil(charge).toInt())
            } else {
                discountAmount.set(0)
            }
        } else {
            val discountValue = discountAmount.get()
            if (discountValue != 0) {
                val charge = discountValue / conversionRate
                discountAmount.set(ceil(charge).toInt())
            }
            extraDiscountAmount.set(0)
        }

        val covidTestPrice = covidAmount.get()
        if (covidTestPrice != 0) {
            val charge = covidTestPrice / conversionRate
            covidAmount.set(ceil(charge).toInt())
        }

        val travelInsurancePrice = travelInsuranceAmount.get()
        if (travelInsurancePrice != 0) {
            val charge = covidTestPrice / conversionRate
            travelInsuranceAmount.set(ceil(charge).toInt())
        }

        val aitCharge = advanceIncomeTax.get()
        if (aitCharge != 0) {
            val amount = aitCharge / conversionRate
            advanceIncomeTax.set(ceil(amount).toInt())
        }

        val baggageCharge = totalBaggageCharge.get()
        if (baggageCharge != 0) {
            val charge = baggageCharge / conversionRate
            totalBaggageCharge.set(ceil(charge).toInt())
        } else {
            totalBaggageCharge.set(baggageCharge)
        }
        calculateTotalPrice()
    }

    fun updateWithBDTPayment() {
        if (currency.get() == PaymentGatewayType.USD.toString()) {
            advanceIncomeTax.set(ceil(advanceIncomeTaxBDT).toInt())
            covidAmount.set(covidAmountBDT)
            travelInsuranceAmount.set(travelInsuranceAmountBDT)
            baggageInsuranceAmount.set(baggageInsuranceAmountBDT)
            totalBaggageCharge.set(ceil(advanceIncomeTaxBDT).toInt())
            discountAmount.set(discountAmountBDT)
        }
        currency.set(PaymentGatewayType.BDT.toString())
        calculateTotalPrice()
        priceBreakdownMsg.set(PriceBreakDownUtil.getFormattedPriceBreakDown(flightsInfo.priceBreakdown))
        currency.notifyChange()
        priceBreakdownMsg.notifyChange()
    }

    companion object {
        const val GOTO_PAYMENT = "GOTO_PAYMENT"
        const val GOTO_TERMS = "GOTO_TERMS"
        const val GOTO_PRIVACY = "GOTO_PRIVACY"
        const val GOTO_FLIGHT_DASHBOARD = "GOTO_FLIGHT_DASHBOARD"
    }
}
