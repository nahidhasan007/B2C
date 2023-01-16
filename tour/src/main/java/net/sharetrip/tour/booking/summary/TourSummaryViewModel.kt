package net.sharetrip.tour.booking.summary

import android.view.View
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.coupon.CouponRequest
import net.sharetrip.shared.model.coupon.CouponResponse
import net.sharetrip.payment.model.PaymentMethod
import com.sharetrip.base.network.model.*
import net.sharetrip.shared.utils.DateUtil
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.model.TourBookingParam
import net.sharetrip.tour.model.TourBookingResponse
import net.sharetrip.tour.model.TourSummary
import kotlin.math.roundToInt

class TourSummaryViewModel(
    private val bookingParam: TourBookingParam,
    val summary: TourSummary,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repo: TourSummaryRepo
) : BaseOperationalViewModel() {

    private var gateWayId = ""

    val progressBar = ObservableBoolean(false)
    val paymentList = MutableLiveData<List<PaymentMethod>>()
    var dataListPack = ArrayList<PaymentMethod>()
    var tourcoupon: String = ""
    var couponRequest: CouponRequest? = null
    val redeemInfo = ObservableInt()
    val totalPrice = ObservableInt()
    val redeemChecked = MutableLiveData<Boolean>()
    val isCardSelected = ObservableBoolean(true)
    val isReedemSelected = ObservableBoolean(false)
    val isCouponSelected = ObservableBoolean(false)
    val wannaRedeem = ObservableBoolean()
    val isCouponViewShow = ObservableBoolean(false)
    val couponObserver = ObservableField<CouponRequest>()
    val cardPaymentChecked = MutableLiveData<Boolean>()
    val availCoupon = MutableLiveData<Boolean>()
    val showMsg = MutableLiveData<String>()

    init {
        redeemInfo.set(summary.earnTripCoin)
        totalPrice.set(summary.totalCost)
        couponObserver.set(CouponRequest())
        fetchPaymentGatewayInfo()
    }

    val seekBarListener = object : OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            redeemInfo.set(progress)
            totalPrice.set(summary.totalCost - progress)
        }
    }

    fun onCouponApply() {
        couponRequest = couponObserver.get()
        couponRequest?.let {
            it.deviceType = "Android"
            it.serviceType = "Tour"
            onCouponRequest(it)
        }
    }

    private fun updateCouponDetailsUI(discount: String, discountType: String) {
        when (discountType) {
            "Flat" -> {
                couponDiscount = discount.toInt()
            }
            "Percentage" -> {
                couponDiscount = ((discount.toDouble() / 100) * summary.totalCost).roundToInt()
            }
        }

        onCouponChecked()
    }

    fun onCouponChecked() {
        totalPrice.set(summary.totalCost - couponDiscount)
        redeemInfo.set(couponDiscount)
    }

    fun onCouponRequest(couponReq: CouponRequest) {
        val token = sharedPrefsHelper.get(PrefKey.ACCESS_TOKEN, "")

        executeSuspendedCodeBlock(OperationTags.VALIDATE_COUPON.name) {
            repo.validateCoupon(token, couponReq)
        }
    }

    private fun fetchPaymentGatewayInfo() {
        executeSuspendedCodeBlock(OperationTags.GET_PAYMENT_GETWAYS.name) {
            repo.getPaymentGateways(GatewayService.Tour.name)
        }
    }

    fun onClickBookingTour() {
        if (gateWayId.isEmpty()) return
        bookingParam.gateway = gateWayId
        bookingParam.apply {
            if (DateUtil.isDateIsValid(tourDate)) {
                this.tourDate = DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(tourDate)
            }
            coupon = if (isCouponSelected.get()) tourcoupon else ""

        }
        progressBar.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock(OperationTags.BOOK_TOUR.name) {
            repo.bookTour(token, bookingParam)
        }
    }

    fun setPaymentGateWayId(id: String) {
        gateWayId = id
    }

    private var couponDiscount = 0

    fun onClickCheckBox(view: View) {
        if (view is RadioButton) {
            when (view.id) {
                R.id.redeem_check_box -> {
                    paymentList.value = dataListPack.filter { it.series.isEmpty() }
                    isCardSelected.set(false)
                    isReedemSelected.set(true)
                    isCouponSelected.set(false)
                    wannaRedeem.set(true)
                    redeemChecked.value = true
                    totalPrice.set(summary.totalCost)
                    redeemInfo.set(0)
                }
                R.id.radio_button_card_payment -> {
                    paymentList.value = dataListPack.filter { it.series.isEmpty() }
                    isCardSelected.set(true)
                    isReedemSelected.set(false)
                    isCouponSelected.set(false)
                    cardPaymentChecked.value = true
                    wannaRedeem.set(false)
                }
                R.id.radio_button_coupon -> {
                    paymentList.value = dataListPack.filter { it.isCouponAvailable }
                    wannaRedeem.set(false)
                    isCardSelected.set(false)
                    isReedemSelected.set(false)
                    isCouponSelected.set(true)
                    availCoupon.value = true
                    totalPrice.set(summary.totalCost - couponDiscount)
                    redeemInfo.set(couponDiscount)
                }
            }
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            OperationTags.BOOK_TOUR.name -> {
                val bookingResponse = (data.body as RestResponse<*>).response as TourBookingResponse
                //TODO:: activitySwitcher.open(PaymentScreen(bookingResponse.url!!, false))
            }
            OperationTags.GET_PAYMENT_GETWAYS.name -> {
                val paymentGatewayRes =
                    (data.body as RestResponse<*>).response as List<PaymentMethod>
                dataListPack = paymentGatewayRes as ArrayList<PaymentMethod>
                paymentList.value = paymentGatewayRes.filter { it.series.isEmpty() }
                isCouponViewShow.set(paymentGatewayRes.none { it.isCouponAvailable })
                dataLoading.set(false)
            }
            OperationTags.VALIDATE_COUPON.name -> {
                val couponResponse = (data.body as RestResponse<*>).response as CouponResponse
                tourcoupon = couponRequest!!.coupon
                updateCouponDetailsUI(couponResponse.discount, couponResponse.discountType)
                showMsg.value = "Coupon added successfully"
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            OperationTags.BOOK_TOUR.name ->
                progressBar.set(false)

            OperationTags.GET_PAYMENT_GETWAYS.name -> {}

            OperationTags.VALIDATE_COUPON.name -> {
                progressBar.set(false)
                showMsg.value = errorMessage
            }
        }
    }

    private enum class OperationTags { BOOK_TOUR, GET_PAYMENT_GETWAYS, VALIDATE_COUPON }
}
