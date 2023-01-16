package net.sharetrip.bus.booking.view.summary

import android.view.View
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.GatewayService
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.booking.model.payment.PaymentMethod
import net.sharetrip.bus.network.BusBookingApiService
import net.sharetrip.bus.utils.MsgUtils.acceptTerms
import net.sharetrip.bus.utils.MsgUtils.selectPaymentMethod
import net.sharetrip.bus.utils.SingleLiveEvent


class BookingSummaryViewModel(
    var departureInfo: Departure,
    var passengerInfo: ItemTraveler,
    val searchIdAndTripCoin: SearchIdAndTripCoin,
    private val apiService: BusBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    val totalAmount = ObservableField("0")
    val discount = ObservableField("0")
    val maxRedeemCoin = SingleLiveEvent<String>()
    val dialogLoading = SingleLiveEvent<Boolean>()
    val navigateToPayment = SingleLiveEvent<String>()
    var isRedeemClicked = MutableLiveData(false)
    var isCouponClicked = MutableLiveData(false)
    var isEarnAndAvailClicked = MutableLiveData(false)
    val paymentMethodList = MutableLiveData<List<PaymentMethod>>()
    var prevSelectedMethod = 0
    var paymentMethodWithEarn: MutableList<PaymentMethod> = ArrayList()
    var paymentMethodWithRedeem: MutableList<PaymentMethod> = ArrayList()
    var paymentMethodWithCoupon: MutableList<PaymentMethod> = ArrayList()
    var paymentMethodListFinal: MutableList<PaymentMethod> = ArrayList()
    var isAcceptedTerms = false
    var selectedSeries = 0
    var isTripInfoExpanded = ObservableBoolean(false)
    var isOptionExpanded = ObservableBoolean(false)
    var isRedeemChecked = ObservableBoolean(false)
    var isCouponChecked = ObservableBoolean(false)
    var isEarnAndAvailChecked = ObservableBoolean(false)
    var seekValue = ObservableField("0")
    private var prevSelectedMethodId: Int? = null
    val selectedCoupon = ObservableField<String>()

    init {
        totalAmount.set(departureInfo.totalPayable)
        discount.set(departureInfo.discount)
        getRedeemCoin()
        fetchPaymentGateWay()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dialogLoading.value = false

        when (operationTag) {
            SummaryApiCallingKey.PaymentGate.name -> {
                val response = data.body as RestResponse<*>
                paymentMethodListFinal =
                    response.response!! as MutableList<PaymentMethod>
                paymentMethodList.value = response.response!! as List<PaymentMethod>
                initDiscountOption()

            }
            SummaryApiCallingKey.GeneratePaymentUrl.name -> {
                val response = data.body as RestResponse<*>

                if (response.code == "SUCCESS") {
                    navigateToPayment.value = response.response as String
                } else {
                    showMessage(response.message)
                }
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dialogLoading.value = false
        showMessage(errorMessage)
    }

    private fun getRedeemCoin() {
        var pointsRedeemable = searchIdAndTripCoin.redeemableTripCoin
        var pointsAvailable = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
        pointsAvailable = pointsAvailable.filter { it in '0'..'9' }

        if (pointsAvailable.isEmpty() || pointsAvailable.isBlank())
            pointsAvailable = "0"
        if (pointsAvailable.toInt() - pointsRedeemable.toInt() < 0)
            pointsRedeemable = pointsAvailable

        maxRedeemCoin.value = pointsRedeemable
    }

    fun onClickCheckBox(view: View) {
        if (view is RadioButton) {
            prevSelectedMethodId = view.id
            when (view.id) {
                R.id.redeem_check_box -> {
                    isCouponChecked.set(false)
                    isEarnAndAvailChecked.set(false)
                    isRedeemChecked.set(true)

                    isCouponClicked.value = false
                    isEarnAndAvailClicked.value = false
                    isRedeemClicked.value = true
                    totalAmount.set((departureInfo.totalPayable!!.toDouble() - departureInfo.nonNullDiscount.toDouble()).toString())
                    discount.set("0")
                    totalAmount.set(
                        (totalAmount.get()!!.toDouble() - seekValue.get()!!
                            .toInt()).toString()
                    )
                }

                R.id.radio_button_earn_and_avail -> {
                    isCouponChecked.set(false)
                    isEarnAndAvailChecked.set(true)
                    isRedeemChecked.set(false)

                    isCouponClicked.value = false
                    isEarnAndAvailClicked.value = true
                    isRedeemClicked.value = false
                    totalAmount.set(departureInfo.totalPayable)
                    discount.set(departureInfo.discount)
                }

                R.id.radio_button_coupon -> {
                    isCouponChecked.set(true)
                    isEarnAndAvailChecked.set(false)
                    isRedeemChecked.set(false)

                    isCouponClicked.value = true
                    isEarnAndAvailClicked.value = false
                    isRedeemClicked.value = false
                    totalAmount.set((departureInfo.totalPayable!!.toDouble() - departureInfo.nonNullDiscount.toDouble()).toString())
                    discount.set("0")
                }
            }
        }
    }

    fun onExpandDiscountOption() {
        isOptionExpanded.set(!isOptionExpanded.get())
    }

    fun onExpandTripInfo() {
        isTripInfoExpanded.set(!isTripInfoExpanded.get())
    }

    fun onTermsClicked() {
        isAcceptedTerms = !isAcceptedTerms
    }

    fun onPayNowClicked() {
        if (isAcceptedTerms) {
            if (prevSelectedMethod >= 0) {
                var series = ""
                if (paymentMethodListFinal[prevSelectedMethod].series.isNotEmpty())
                    series =
                        paymentMethodListFinal[prevSelectedMethod].series[selectedSeries].toString()
                val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
                var bookRequest: BookingRequest? = null
                if (isRedeemChecked.get() && seekValue.get()?.toInt()!! > 0) {
                    bookRequest =
                        BookingRequest(
                            searchId = searchIdAndTripCoin.searchId,
                            cartId = departureInfo.cartId!!,
                            gateWay = paymentMethodListFinal[prevSelectedMethod].id,
                            cardSeries = series,
                            coupon = null,
                            tripCoin = seekValue.get()?.toInt()
                        )
                } else if (isCouponChecked.get() && !selectedCoupon.get().isNullOrEmpty()) {
                    bookRequest =
                        BookingRequest(
                            searchId = searchIdAndTripCoin.searchId,
                            cartId = departureInfo.cartId!!,
                            gateWay = paymentMethodListFinal[prevSelectedMethod].id,
                            cardSeries = series,
                            coupon = null,
                            tripCoin = null
                        )
                } else {
                    bookRequest =
                        BookingRequest(
                            searchId = searchIdAndTripCoin.searchId,
                            cartId = departureInfo.cartId!!,
                            gateWay = paymentMethodListFinal[prevSelectedMethod].id,
                            cardSeries = series,
                            coupon = null,
                            tripCoin = null
                        )
                }
                dialogLoading.value = true

                executeSuspendedCodeBlock(SummaryApiCallingKey.GeneratePaymentUrl.name) {
                    apiService.postBookingRequest(
                        token = token,
                        busBooking = bookRequest
                    )
                }
            } else
                showMessage(selectPaymentMethod)
        } else
            showMessage(acceptTerms)
    }

    private fun fetchPaymentGateWay(gateway: List<String>? = null) {
        dialogLoading.value = true

        executeSuspendedCodeBlock(SummaryApiCallingKey.PaymentGate.name) {
            apiService.fetchPaymentGateway(
                GatewayService.Bus.name,
                "BDT",
                gateway
            )
        }
    }

    fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekValue.set(progress.toString())
        val newValue =
            departureInfo.totalPayable!!.toDouble() - departureInfo.nonNullDiscount.toDouble() - progress
        totalAmount.set(String.format("%.2f", newValue))
    }

    fun changePaymentSelected(pos: Int) {
        paymentMethodListFinal[pos].checked = true
        if (prevSelectedMethod >= 0 && prevSelectedMethod != pos) {
            paymentMethodListFinal[prevSelectedMethod].checked = false
        }
    }

    private fun initDiscountOption() {
        dialogLoading.value = false
        isCouponChecked.set(false)
        isEarnAndAvailChecked.set(true)
        isRedeemChecked.set(false)
        isCouponClicked.value = false
        isEarnAndAvailClicked.value = true
        isRedeemClicked.value = false
        isOptionExpanded.set(!isOptionExpanded.get())
    }

    fun negate(i: String): String {
        return (-i.toInt()).toString()
    }
}
