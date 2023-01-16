package net.sharetrip.visa.booking.view.summary

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.*
import net.sharetrip.shared.utils.ARG_PAYMENT_URL
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.SERVICE_TYPE
import net.sharetrip.shared.utils.SERVICE_TYPE_VISA
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.*
import net.sharetrip.visa.booking.model.coupon.CouponRequest
import net.sharetrip.visa.booking.model.coupon.CouponResponse
import net.sharetrip.visa.booking.model.payment.PaymentMethod
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.MsgUtils.EARN_TRIP_COIN
import net.sharetrip.visa.utils.MsgUtils.REDEEM_COUPON
import net.sharetrip.visa.utils.MsgUtils.REDEEM_TRIP_COIN
import net.sharetrip.visa.utils.SingleLiveEvent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class VisaBookingSummaryViewModel(
    private val visaQuery: VisaSearchQuery,
    private val apiService: VisaBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private var flag = false
    private var isRedeemCodeActivated = false
    private var userTripCoin = 0
    private var total = 0.0
    private var totalPayable = 0.0
    private var dateWithTraveler = ""
    private val discountName = ObservableField<String>()
    private var discountModel = VisaDiscountModel()
    private lateinit var product: VisaProductsItem
    private var couponReq: CouponRequest? = null
    private val totalPrice = ObservableInt()
    private val eventManager =
        AnalyticsProvider.eventManager(AnalyticsProvider.getFirebaseAnalytics())
    private var totalAfterDiscount = 0
    private val convenienceVatFlag = false

    val paymentMethodList = MutableLiveData<Event<List<PaymentMethod>>>()
    val clickedBooking = MutableLiveData<Event<Boolean>>()
    val redeemChecked = MutableLiveData<Boolean>()
    val availCoupon = MutableLiveData<Boolean>()
    val cardPaymentChecked = MutableLiveData<Boolean>()
    val seekBarMax = MutableLiveData<String>()
    val observableUserTripCoin = SingleLiveEvent<String>()
    val navigateToPayment = MutableLiveData<Event<Bundle>>()
    val isCheckboxActive = ObservableBoolean()
    val isCardSelected = ObservableBoolean(false)
    val isRedeemSelected = ObservableBoolean(false)
    val isCouponSelected = ObservableBoolean(false)
    val isCouponShow = ObservableBoolean(false)
    val isVisaSummaryExpand = ObservableBoolean(false)
    val isDiscountOptionExpand = ObservableBoolean(false)
    var isStickerVisa = ObservableBoolean(false)
    var isPaymentMethodEmpty = ObservableBoolean(false)
    val wannaRedeem = ObservableBoolean()
    val discountAmount = ObservableInt()
    val couponObserver = ObservableField<CouponRequest>()
    val visaInfoObservable = ObservableField<String>()
    val visaDateAndTravelerObservable = ObservableField<String>()
    val earnText = ObservableField<String>()
    val redeemText = ObservableField<String>()
    val couponText = ObservableField<String>()
    val totalPayableAmounts = ObservableField<String>()
    val discount = ObservableField<String>()
    val totalAmount = ObservableField<String>()
    val travellerTitle = ObservableField<String>()
    val visaFeeTitle = ObservableField<String>()
    val courierTitle = ObservableField<String>()
    val visaFeeValueText = ObservableField<String>()
    val processingFeeTitle = ObservableField<String>()
    val processingFeeValueText = ObservableField<String>()
    val courierChargeValueText = ObservableField<String>()
    val bookingCurrency = ObservableField<String>()
    val redeemCoin = ObservableInt(0)
    var progressForTripCoin = 0
    var couponDiscount = 0
    var token = ""
    var entryDate = ""
    var exitDate = ""
    var validity = ""
    var maxStay = ""
    var bankDiscountInfo = ""
    var visaCoupon = ""
    val isConvenienceVisible = ObservableBoolean(false)
    val totalConvenienceCharge = ObservableField(0)
    val vatCharge = ObservableField(0)
    val selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    var isExceptionOccurred = MutableLiveData<Boolean>()
    val navigateProfile = SingleLiveEvent<String>()
    val isRedeemShow = ObservableBoolean(true)

    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (flag) {
                if (userTripCoin >= progress) {
                    redeemCoin.set(progress)
                    discountAmount.set(progress)
                    progressForTripCoin = progress

                    totalPayable = total - progress

                    updateTotalPayableWithConvenience()

                    discount.set(progress.toString())

                    observableUserTripCoin.value =
                        NumberFormat.getNumberInstance(Locale.US).format((userTripCoin - progress))
                } else {
                    redeemCoin.set(userTripCoin)
                    observableUserTripCoin.value = NumberFormat.getNumberInstance(Locale.US)
                        .format((userTripCoin - userTripCoin))
                }
            }

            flag = true
        }
    }

    init {
        eventManager.initialCheckoutVisa()
        token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        initializeUIData()
        calculatePrice(1.0)
        fetchPaymentGateWay()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)

        when (operationTag) {
            BookingSummaryApiCallingKey.PaymentGateWay.name -> {
                val response = (data.body as RestResponse<*>).response as List<PaymentMethod>
                paymentMethodList.value = Event(response)

                when (discountModel.type) {
                    "Coupon" -> {
                        totalAfterDiscount = 1000
                        couponDiscount = discountModel.discountAmount
                        val couponRequest = CouponRequest()
                        couponRequest.let {
                            it.deviceType = "Android"
                            it.serviceType = "Visa"
                            it.coupon = discountModel.coupon
                            couponObserver.set(couponRequest)
                        }
                        availCoupon.value = true
                    }

                    "Coin" -> {
                        redeemCoin.set(discountModel.discountAmount)
                        progressForTripCoin = discountModel.discountAmount
                        redeemChecked.value = true
                    }

                    else ->
                        cardPaymentChecked.value = true
                }
            }

            BookingSummaryApiCallingKey.BookingRequest.name -> {
                val response = (data.body as RestResponse<*>).response as VisaBookingResponse
                val bundle = Bundle()
                bundle.putString(ARG_PAYMENT_URL, response.url)
                bundle.putString(SERVICE_TYPE, SERVICE_TYPE_VISA)
                navigateToPayment.value = Event(bundle)
            }

            BookingSummaryApiCallingKey.Coupon.name -> {
                val response = (data.body as RestResponse<*>).response as CouponResponse
                couponReq?.let { visaCoupon = it.coupon }
                updateCouponDetailsUI(response.discount, response.discountType)
                showMessage("Coupon added successfully")
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)

        when (operationTag) {

            BookingSummaryApiCallingKey.PaymentGateWay.name -> {
                isPaymentMethodEmpty.set(true)
            }

            BookingSummaryApiCallingKey.Coupon.name -> {
                updateCouponDetailsUI(50.toString(), "Percentage")
            }
        }
    }

    private fun getDiscountAmongTravellers(): Double? =
        product.discountPrice?.times(visaQuery.travellerCount)

    private fun getMyTripCoins() = userTripCoin

    private fun makeJson(): String {
        visaQuery.visaSelection = null
        visaQuery.currentTravellerPosition = null
        visaQuery.selectedVisaType = null
        visaQuery.travelDataFullInfo = null
        visaQuery.totalAmount = null
        visaQuery.nationality = null
        visaQuery.visaPrepMinDays = null

        val gson = Gson()
        return gson.toJson(visaQuery)
    }

    private fun initializeUIData() {
        if (visaQuery.tripCoin <= 0)
            isRedeemShow.set(false)

        try {
            product = visaQuery.visaSelection!!.visaProducts?.get(visaQuery.selectedVisaType!!)!!
        } catch (e: Exception) {
            isExceptionOccurred.value = true
            return
        }
        earnText.set(EARN_TRIP_COIN)
        redeemText.set(REDEEM_TRIP_COIN)
        couponText.set(REDEEM_COUPON)

        val amount = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
        observableUserTripCoin.value = amount

        userTripCoin = if (amount.isEmpty()) {
            0
        } else {
            amount.filter { it in '0'..'9' }.toInt()
        }

        visaInfoObservable.set(product.title)
        entryDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(visaQuery.entryDate)

        if (visaQuery.exitDate.isNotEmpty()) {
            exitDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(visaQuery.exitDate)
            dateWithTraveler =
                entryDate + "-" + exitDate + ", ${visaQuery.travellerCount} Traveller(s)"
        } else {
            exitDate = ""
            dateWithTraveler = entryDate + ", ${visaQuery.travellerCount} Traveller(s)"
        }

        dateWithTraveler = entryDate + "-" + exitDate + ", ${visaQuery.travellerCount} Traveller(s)"
        validity = "${product.validityDays} Days"
        maxStay = "${product.maxStayDays} Days"
        visaDateAndTravelerObservable.set(dateWithTraveler)
        bookingCurrency.set(product.bookingCurrency)

        if (getMyTripCoins() > visaQuery.visaSelection!!.points.earning) {
            seekBarMax.value = visaQuery.visaSelection!!.points.earning.toString()
        } else {
            seekBarMax.value = getMyTripCoins().toString()
        }
    }

    private fun onCouponRequest() {
        dataLoading.set(true)

        executeSuspendedCodeBlock { apiService.getValidateCoupon(token, couponReq!!) }
    }

    private fun fetchPaymentGateWay() {
        dataLoading.set(true)

        executeSuspendedCodeBlock(BookingSummaryApiCallingKey.PaymentGateWay.name) {
            apiService.fetchPaymentGateway(
                GatewayService.Visa.name
            )
        }
    }

    fun makeABookingRequest(paymentId: String, cardSeries: String) {
        visaQuery.gateway = paymentId
        visaQuery.cardSeries = cardSeries
        visaQuery.tripCoin = if (isRedeemCodeActivated) redeemCoin.get() else 0
        val body: RequestBody = makeJson().toRequestBody("application/json".toMediaTypeOrNull())
        dataLoading.set(true)

        executeSuspendedCodeBlock(BookingSummaryApiCallingKey.BookingRequest.name) {
            apiService.bookAVisaRequest(
                token,
                body
            )
        }
    }

    fun calculatePrice(withConversionRate: Double) {

        if (this::product.isInitialized) {

            if (product.type.equals(VisaType.StickerVisa.productName)) {
                isStickerVisa.set(true)
            } else {
                isStickerVisa.set(false)
            }

            val visaFee = product.visaFee.times(visaQuery.travellerCount) / withConversionRate
            visaFeeValueText.set(NumberFormat.getNumberInstance(Locale.US).format(visaFee))

            val processFee =
                product.processingFee.times(visaQuery.travellerCount) / withConversionRate
            processingFeeValueText.set(NumberFormat.getNumberInstance(Locale.US).format(processFee))

            val courierCharge =
                product.courierCharge.times(visaQuery.travellerCount) / withConversionRate
            courierChargeValueText.set(
                NumberFormat.getNumberInstance(Locale.US).format(courierCharge)
            )

            total = visaFee + processFee + courierCharge
            totalAmount.set(NumberFormat.getNumberInstance(Locale.US).format(total))

            travellerTitle.set("Travelers x ${visaQuery.travellerCount}")
            visaFeeTitle.set("Visa Fee  x ${visaQuery.travellerCount}")
            courierTitle.set("Courier Charge x ${visaQuery.travellerCount}")

            val processingTitle = "Share Trip Processing  Fee x ${visaQuery.travellerCount}"
            processingFeeTitle.set(processingTitle)

            totalPayable = (total - getDiscountAmongTravellers()!! / withConversionRate)
            totalPayableAmounts.set(NumberFormat.getNumberInstance(Locale.US).format(totalPayable))
            discount.set(
                NumberFormat.getNumberInstance(Locale.US).format(getDiscountAmongTravellers())
            )

            updateTotalPayableWithConvenience()

            visaQuery.totalAmount = totalPayable
        }
    }

    private fun updateTotalPayableWithConvenience() {
        if (selectedPaymentMethod.value?.charge == null || selectedPaymentMethod.value?.charge == 0.0) {
            totalConvenienceCharge.set(0)
            vatCharge.set(0)
            isConvenienceVisible.set(false)
        } else {
            val convenienceData = calculateConvenience(totalPayable)
            totalConvenienceCharge.set(ceil(convenienceData.first).toInt())
            vatCharge.set(ceil(convenienceData.second).toInt())
            isConvenienceVisible.set(true)
            totalPayable = convenienceData.third
        }

        totalPayableAmounts.set(
            NumberFormat.getNumberInstance(Locale.US).format(ceil(totalPayable).toInt())
        )
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

    fun getTotalPayableAmount(): Double? = visaQuery.totalAmount

    fun onCouponApply() {
        couponReq = couponObserver.get()
        couponReq?.let {
            it.deviceType = "Android"
            it.serviceType = "Visa"
            onCouponRequest()
        }
    }

    fun onClickTermsAndConditionCheckbox(view: View) {
        isCheckboxActive.set((view as AppCompatCheckBox).isChecked)
    }

    fun onClickPrivacyPolicy() {
        navigateProfile.value = ProfileAction.ARG_CONTENT_PRIVACY
    }

    fun onClickTermsAndCondition() {
        navigateProfile.value = ProfileAction.ARG_CONTENT_TERM
    }

    private fun updateCouponDetailsUI(discount: String, discountType: String) {
        when (discountType) {

            "Flat" -> {
                couponDiscount = discount.toInt()
                totalPrice.set(totalAfterDiscount)
                discountAmount.set(discount.toInt())
            }

            "Percentage" -> {
                totalPrice.set(totalAfterDiscount)
                discountAmount.set(couponDiscount)
            }
        }
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

    fun onVisaSummaryLayoutClick() {
        isVisaSummaryExpand.set(!isVisaSummaryExpand.get())
    }

    fun onChangeBtnClick() {
        isDiscountOptionExpand.set(!isDiscountOptionExpand.get())
    }

    fun onBookNowClick() {
        clickedBooking.value = Event(true)
    }

    fun onCouponChecked() {
        discountName.set(couponText.get())
        wannaRedeem.set(false)
        isCardSelected.set(false)
        isRedeemSelected.set(false)
        isCouponSelected.set(true)
        totalPrice.set(totalAfterDiscount)
        discountAmount.set(couponDiscount)
        isRedeemCodeActivated = false
        observableUserTripCoin.value =
            NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
    }

    fun onRedeemChecked() {
        discountName.set(redeemText.get())
        isCardSelected.set(false)
        isRedeemSelected.set(true)
        isCouponSelected.set(false)
        wannaRedeem.set(true)

        totalPayable = total - redeemCoin.get().toDouble()
        totalPayableAmounts.set(NumberFormat.getNumberInstance(Locale.US).format(totalPayable))
        discount.set(redeemCoin.get().toString())

        isRedeemCodeActivated = true
        observableUserTripCoin.value =
            NumberFormat.getNumberInstance(Locale.US).format((userTripCoin - progressForTripCoin))

        updateTotalPayableWithConvenience()
    }

    fun onCardChecked() {
        discountName.set(earnText.get())
        isCardSelected.set(true)
        isRedeemSelected.set(false)
        isCouponSelected.set(false)
        wannaRedeem.set(false)

        totalPayable = total - getDiscountAmongTravellers()!!
        totalPayableAmounts.set(NumberFormat.getNumberInstance(Locale.US).format(totalPayable))
        discount.set(getDiscountAmongTravellers().toString())

        isRedeemCodeActivated = false
        observableUserTripCoin.value =
            NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))

        calculatePrice(1.0)
    }
}
