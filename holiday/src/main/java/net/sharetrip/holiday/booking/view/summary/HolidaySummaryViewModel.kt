package net.sharetrip.holiday.booking.view.summary

import android.view.View
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.os.bundleOf
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.holiday.R
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.coupon.CouponRequest
import com.sharetrip.base.network.model.Status
import com.sharetrip.base.utils.ShareTripAppConstants.TWO_DIGIT_DECIMAL_FORMAT
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.holiday.booking.model.*
import net.sharetrip.payment.model.PaymentGatewayType
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.shared.utils.*
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class HolidaySummaryViewModel(
    private val param: HolidayParam,
    val contact: PrimaryContact,
    val summary: HolidaySummary,
    val tripCoin: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    val repository: HolidaySummaryRepository
) : BaseViewModel() {
    private val bookingParam = HolidayBookingParam()
    private var gateWayId = ""
    private val userTripCoin: Int
    private var couponDiscount = 0.0
    private val availCoupon = MutableLiveData<Boolean>()
    private var flag = false
    private val isCheckboxActive = ObservableBoolean(false)
    private val _tripCoinText = MutableLiveData<String>()
    val tripCoinText: LiveData<String>
        get() = _tripCoinText

    val isDataLoading = ObservableBoolean(false)
    val holidayParamObserver = ObservableField<HolidayParam>()
    val isCardSelected = ObservableBoolean(true)
    val isRedeemSelected = ObservableBoolean(false)
    val isCouponSelected = ObservableBoolean(false)
    val wannaRedeem = ObservableBoolean()
    val isCouponShow = repository.isCouponShow
    val redeemInfo = ObservableInt()
    val redeemCoin = ObservableInt()
    val totalPrice = ObservableInt()
    val couponObserver = ObservableField<CouponRequest>()
    val discount = ObservableInt()
    val totalPriceWithoutDiscount = ObservableDouble()
    val isHolidaySummaryExpand = ObservableBoolean(false)
    val isDiscountOptionExpand = ObservableBoolean(false)
    val isConvenienceVisible = ObservableBoolean(false)
    val totalConvenienceCharge = ObservableField(0)
    val vatCharge = ObservableField(0)
    val selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    val earnText = ObservableField<String>()
    val redeemText = ObservableField<String>()
    val couponText = ObservableField<String>()
    val paymentList = repository.paymentList
    val redeemChecked = MutableLiveData<Boolean>()
    val seekBarMax = MutableLiveData<String>()
    val usdPayment = MutableLiveData<Double>()
    val currency = ObservableField<String>()
    var convertRate = 0.0
    val payButtonText = ObservableField("Pay Now")
    var discountAmountBDT = 0
    var bankDiscountInfo = ""
    var cardSeries = String()

    private var holidayCoupon = ""
    private var dataListPack = repository.dataListPack
    private val eventManager =
        AnalyticsProvider.eventManager(AnalyticsProvider.getFirebaseAnalytics())
    private val convenienceVatFlag = false

    init {
        eventManager.initialCheckoutHoliday()
        holidayParamObserver.set(param.copy())
        couponObserver.set(CouponRequest())
        redeemInfo.set(0)
        val amount = tripCoin.replace(",", "")
        userTripCoin = amount.toInt()

        earnText.set("I want to Earn Trip Coin Avail Discount Offer")
        redeemText.set("I want to Redeem Trip Coins")
        couponText.set("I want to Redeem Coupon")

        if (getMyTripCoins() > summary.earnTripCoin) {
            seekBarMax.value = summary.earnTripCoin.toString()
        } else {
            seekBarMax.value = getMyTripCoins().toString()
        }

        bookingParam.apply {
            packageId = param.holidayId
            packagePeriodId = param.offerId
            packageDate = DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(param.packageDate)

            adultsCount = param.adult
            infantCount = param.infant
            child3To6Count = param.child3to6
            child7To12Count = param.child7to12

            singleRoomCount = param.singleRoom
            doubleRoomCount = param.doubleRoom
            twinRoomCount = param.twinRoom
            tripleRoomCount = param.tripleRoom
            quadRoomCount = param.quadRoom

            arrivalTransportType = param.arrivalType
            arrivalTransportName = param.arrivalTransportName
            arrivalTransportCode = param.arrivalTransportCode
            arrivalTime = param.arrivalPickupTime

            departureTransportType = param.departureType
            departureTransportName = param.departureTransportName
            departureTransportCode = param.departureTransportCode
            departureTime = param.departureTime
            departurePickupTime = param.pickupTime
            departureAdditionalText = "Empty Text"

            searchID = param.searchID
            primaryContact = contact
        }

        discount.set(ceil(summary.totalCost - summary.discountedPrice).toInt())
        discountAmountBDT = ceil(summary.totalCost - summary.discountedPrice).toInt()

        fetchPaymentGatewayInfo()
        calculateTotalPayable()
    }

    fun onPackageSummaryLayoutClick() {
        isHolidaySummaryExpand.set(!isHolidaySummaryExpand.get())
    }

    fun onChangeBtnClick() {
        isDiscountOptionExpand.set(!isDiscountOptionExpand.get())
    }

    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (flag) {
                if (userTripCoin >= progress) {
                    if (currency.get() == PaymentGatewayType.USD.name) {
                        redeemInfo.set(progress)
                        discount.set((progress / convertRate).toInt())
                        calculateTotalPayable()
                        return
                    }

                    redeemCoin.set(progress)
                    redeemInfo.set(progress)
                    discount.set(progress)
                    discountAmountBDT = progress
                    calculateTotalPayable()
                    _tripCoinText.value =
                        NumberFormat.getNumberInstance(Locale.US).format((userTripCoin - progress))
                } else {
                    redeemInfo.set(0)
                    redeemCoin.set(userTripCoin)
                    _tripCoinText.value = NumberFormat.getNumberInstance(Locale.US)
                        .format((userTripCoin - userTripCoin))
                }
            }
            flag = true
        }
    }

    private fun fetchPaymentGatewayInfo() {
        viewModelScope.launch {
            repository.getPaymentGateways(param)
        }
    }

    fun onClickCheckBookingHoliday() {
        if (currency.get() == PaymentGatewayType.USD.toString()) {
            usdPayment.value = totalPrice.get().toDouble()
        } else {
            bookAHoliday()
        }
    }

    fun bookAHoliday() {
        if (gateWayId.isEmpty()) {
            showMessage("No payment gateway found!")
            return
        } else if (!isCheckboxActive.get()) {
            showMessage("Click to agree on the terms & conditions and privacy policy to continue with your booking.")
            return
        } else {
            bookingParam.gateway = gateWayId
            bookingParam.cardSeries = cardSeries
        }

        bookingParam.apply {
            coupon = if (isCouponSelected.get()) holidayCoupon else ""
        }

        isDataLoading.set(true)

        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getHolidayBookingResponse(token, bookingParam)
            if (repository.apiStatus.value == Status.SUCCESS) {
                val url = repository.holidayBookingResponse.url
                navigateWithArgument(
                    HolidaySummaryNavDestinations.TO_PAYMENT.name,
                    bundleOf(ARG_PAYMENT_URL to url, SERVICE_TYPE to SERVICE_TYPE_VISA)
                )
                isDataLoading.set(false)
            } else {
                isDataLoading.set(false)
            }
        }
    }

    fun setPaymentGateWayId(id: String) {
        gateWayId = id
    }

    private fun getMyTripCoins() = userTripCoin

    fun onCouponApply() {
        val couponRequest = couponObserver.get()
        couponRequest?.let {
            it.deviceType = "Android"
            it.serviceType = "Package"
            onCouponRequest(it)
        }
    }

    private fun onCouponRequest(couponReq: CouponRequest) {
        isDataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getValidateCoupon(token, couponReq)
            if (repository.apiStatus.value == Status.SUCCESS) {
                holidayCoupon = couponReq.coupon
                filterPaymentGateway(repository.couponResponse.value!!.gateway)
                updateCouponDetailsUI(
                    repository.couponResponse.value!!.discount,
                    repository.couponResponse.value!!.discountType
                )
                showMessage("Coupon added successfully")
                isDataLoading.set(false)
            }
            else{
                showMessage(repository.showMsg.value?.getContent().toString())
                isDataLoading.set(false)
            }
        }
    }


    private fun filterPaymentGateway(gatewayList: List<String>) {
        val paymentMethodWithCoupon: MutableList<PaymentMethod> = ArrayList()
        gatewayList.forEach { gateway ->
            dataListPack.value?.forEach { paymentMethod ->
                if (gateway == paymentMethod.id) {
                    paymentMethodWithCoupon.add(paymentMethod)
                }
            }
        }
        if (paymentMethodWithCoupon.isEmpty()) {
            if (gatewayList.isNotEmpty()) {
                setPaymentGateWayId(gatewayList[0])
            }
        }
        paymentList.value = paymentMethodWithCoupon
    }

    private fun updateCouponDetailsUI(discountValue: String, discountType: String) {
        when (discountType) {
            "Flat" -> {
                couponDiscount = discountValue.toDouble()
            }
            "Percentage" -> {
                couponDiscount =
                    ((discountValue.toDouble() / 100) * summary.totalCost).twoDigitDecimal()
            }
        }

        discount.set(ceil(couponDiscount).toInt())
        discountAmountBDT = ceil(couponDiscount).toInt()
        calculateTotalPayable()
    }

    fun onClickCheckBox(view: View) {
        if (view is RadioButton) {

            when (view.id) {

                R.id.radio_button_card_payment -> {
                    paymentList.value = dataListPack.value?.filter { it.isEarnTripCoinApplicable }
                    isCardSelected.set(true)
                    isRedeemSelected.set(false)
                    isCouponSelected.set(false)
                    wannaRedeem.set(false)

                    discount.set(ceil(summary.totalCost - summary.discountedPrice).toInt())
                    discountAmountBDT = discount.get()
                    calculateTotalPayable()

                    _tripCoinText.value =
                        NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
                }

                R.id.redeem_check_box -> {
                    paymentList.value = dataListPack.value?.filter { it.isRedeemTripCoinApplicable }
                    isCardSelected.set(false)
                    isRedeemSelected.set(true)
                    isCouponSelected.set(false)
                    wannaRedeem.set(true)
                    redeemChecked.value = true

                    discount.set(redeemCoin.get())
                    discountAmountBDT = discount.get()
                    calculateTotalPayable()

                    _tripCoinText.value =
                        NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
                }

                R.id.radio_button_coupon -> {
                    paymentList.value = dataListPack.value?.filter { it.isCouponAvailable }
                    isCardSelected.set(false)
                    isRedeemSelected.set(false)
                    isCouponSelected.set(true)
                    availCoupon.value = true
                    wannaRedeem.set(false)

                    discount.set(ceil(couponDiscount).toInt())
                    discountAmountBDT = discount.get()
                    calculateTotalPayable()

                    _tripCoinText.value =
                        NumberFormat.getNumberInstance(Locale.US).format((userTripCoin))
                }
            }
        }
    }

    fun onClickTermsAndConditionCheckbox(view: View) {
        isCheckboxActive.set((view as AppCompatCheckBox).isChecked)
    }

    fun onClickPrivacyPolicy() {
        navigateWithArgument(
            HolidaySummaryNavDestinations.TO_PROFILE_CONTENT_PRIVACY.name,
            ProfileAction.ARG_CONTENT_PRIVACY
        )
    }

    fun onClickTermsAndCondition() {
        navigateWithArgument(
            HolidaySummaryNavDestinations.TO_PROFILE_CONTENT_TERM.name,
            ProfileAction.ARG_CONTENT_PRIVACY
        )
    }

    fun updateUSDPayment(paymentMethod: PaymentMethod) {
        convertRate = paymentMethod.currency.conversion.rate
        val holidayObj = holidayParamObserver.get()!!
        val params = param.copy()

        currency.set(PaymentGatewayType.USD.toString())
        holidayObj.currency = PaymentGatewayType.USD.toString()
        holidayObj.singleRoomPrice = params.singleRoomPrice / convertRate
        holidayObj.doubleRoomPrice = params.doubleRoomPrice / convertRate
        holidayObj.twinRoomPrice = params.twinRoomPrice / convertRate
        holidayObj.tripleRoomPrice = params.tripleRoomPrice / convertRate
        holidayObj.quadRoomPrice = params.quadRoomPrice / convertRate
        holidayObj.infantPrice = params.infantPrice / convertRate
        holidayObj.child3to6Price = params.child3to6Price / convertRate
        holidayObj.child7to12Price = params.child7to12Price / convertRate
        holidayParamObserver.set(holidayObj)
        holidayParamObserver.notifyChange()

        val discountValue = (discountAmountBDT / convertRate).twoDigitDecimal()
        discount.set(ceil(discountValue).toInt())

        calculateTotalPayable()
    }

    fun updateWithBDTPayment() {
        val holidayObj = holidayParamObserver.get()!!
        val params = param.copy()

        currency.set(PaymentGatewayType.BDT.toString())
        holidayObj.currency = PaymentGatewayType.BDT.toString()
        holidayObj.singleRoomPrice = params.singleRoomPrice
        holidayObj.doubleRoomPrice = params.doubleRoomPrice
        holidayObj.twinRoomPrice = params.twinRoomPrice
        holidayObj.tripleRoomPrice = params.tripleRoomPrice
        holidayObj.infantPrice = params.infantPrice
        holidayObj.child3to6Price = params.child3to6Price
        holidayObj.child7to12Price = params.child7to12Price
        holidayParamObserver.set(holidayObj)
        holidayParamObserver.notifyChange()

        discount.set(discountAmountBDT)
        calculateTotalPayable()
    }

    fun calculateTotalPayable() {
        val originalPrice = getOriginPrice()
        val discountValue = discount.get()
        if (discountValue >= originalPrice) {
            totalPriceWithoutDiscount.set(getOriginPrice())
            totalPrice.set(0)
        } else {
            val pair = getPayableAmount()
            totalPriceWithoutDiscount.set(pair.second)

            updateFinalPayableWithConvenience(pair.first)
        }
        setPayButtonText()
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

    private fun getPayableAmount(): Pair<Double, Double> {
        return if (currency.get() == PaymentGatewayType.USD.toString()) {
            Pair(getOriginPriceForUSD() - discount.get(), getOriginPriceForUSD())
        } else {
            Pair(getOriginPrice() - discount.get(), getOriginPrice())
        }
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

    private fun getOriginPrice() = summary.totalCost

    private fun getOriginPriceForUSD(): Double {
        val originalValue = getOriginPrice() / convertRate
        return String.format(TWO_DIGIT_DECIMAL_FORMAT, originalValue).toDouble()
    }

    private fun setPayButtonText() {
        if (totalPrice.get() == 0) {
            payButtonText.set("Book Now")
        } else {
            payButtonText.set("Pay Now")
        }
    }

    enum class HolidaySummaryNavDestinations {
        TO_PROFILE_CONTENT_PRIVACY,
        TO_PROFILE_CONTENT_TERM,
        TO_PAYMENT
    }
}
