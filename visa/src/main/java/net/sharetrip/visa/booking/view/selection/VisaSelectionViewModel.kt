package net.sharetrip.visa.booking.view.selection

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.GuestLoginListener
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.GatewayService
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.DateUtil
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.visa.booking.model.VisaProductsItem
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.model.VisaSelection
import net.sharetrip.visa.booking.model.VisaType
import net.sharetrip.visa.booking.model.payment.PaymentMethod
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.MsgUtils.selectVisaTypeMsg
import net.sharetrip.visa.utils.SingleLiveEvent
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class VisaSelectionViewModel(
    private val visaQuery: VisaSearchQuery,
    private val apiService: VisaBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel(), GuestLoginListener {
    private var total = 0.0
    private var totalPayable = 0.0
    private val allPaymentMethod: MutableLiveData<List<PaymentMethod>> = MutableLiveData()
    var visaSelection = MutableLiveData<VisaSelection>()
    var isViewUpdated = MutableLiveData<Boolean>()
    var isStickerVisa = ObservableBoolean(false)
    val totalAmount = ObservableField<String>()
    val totalPayableAmount = ObservableField<String>()
    val discount = ObservableField<String>()
    val travellers = ObservableField<String>()
    val entryDate = ObservableField<String>()
    val travellerTitle = ObservableField<String>()
    val visaFeeTitle = ObservableField<String>()
    val courierTitle = ObservableField<String>()
    val visaFeeValueText = ObservableField<String>()
    val processingFeeTitle = ObservableField<String>()
    val processingFeeValueText = ObservableField<String>()
    val courierChargeValueText = ObservableField<String>()
    var isDataFound = ObservableBoolean(false)
    val navigateToVisaApplication = SingleLiveEvent<VisaSearchQuery>()
    val navigateLogin = MutableLiveData<Event<Boolean>>()
    var lastSelected = -1
    val isConvenienceVisible = ObservableBoolean(false)
    val totalConvenienceCharge = ObservableField(0)
    val vatCharge = ObservableField(0)
    private val convenienceVatFlag = false

    val isShowDialog: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isDismissDialog: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        totalPayableAmount.set("৳ $totalPayable")
        onFetchVisaSearch()
        fetchPaymentGateWay()
        travellers.set("${visaQuery.travellerCount} Traveller(s)")
        entryDate.set(DateUtil.parseDisplayDateFormatFromApiDateFormat(visaQuery.entryDate))
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            search_visa -> {
                dataLoading.set(false)
                val response = (data.body as RestResponse<*>).response as List<VisaSelection>

                if (response.isNotEmpty()) {
                    isDataFound.set(true)
                    val visa: VisaSelection = response[0]
                    visaQuery.visaSelection = visa
                    visaQuery.searchID = visa.searchID
                    visaSelection.postValue(visa)
                } else {
                    isDataFound.set(false)
                }
            }
            fetch_payment_gateway -> {
                val response = (data.body as RestResponse<*>).response as List<PaymentMethod>
                allPaymentMethod.value = response
            }
        }

    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun onFetchVisaSearch() {
        dataLoading.set(true)

        if (visaQuery.entryDate == visaQuery.exitDate) {
            visaQuery.exitDate = ""
        }

        executeSuspendedCodeBlock(search_visa) {
            apiService.searchVisaTypeForACountry(
                visaQuery.visaCountryCode,
                visaQuery.entryDate,
                visaQuery.exitDate,
                visaQuery.travellerCount,
                visaQuery.nationality!!
            )
        }
    }

    private fun fetchPaymentGateWay() {
        executeSuspendedCodeBlock(fetch_payment_gateway) {
            apiService.fetchPaymentGateway(GatewayService.Visa.name)
        }
    }

    fun onContinueButtonClicked() {
        when {
            sharedPrefsHelper[PrefKey.IS_LOGIN, false] -> {
                if (total == 0.0 || visaQuery.selectedVisaType == -1) {
                    showMessage(selectVisaTypeMsg)
                } else {
                    navigateToVisaApplication.value = visaQuery
                }
            }
            else -> {
                isShowDialog.value = false
            }
        }
    }

    fun calculatePrice(product: VisaProductsItem?, lastSelectedPosition: Int) {

        if (product?.type.equals(VisaType.StickerVisa.productName)) {
            isStickerVisa.set(true)
        } else {
            isStickerVisa.set(false)
        }

        isViewUpdated.value = true

        val visaFee = product!!.visaFee.times(visaQuery.travellerCount)
        visaFeeValueText.set(NumberFormat.getNumberInstance(Locale.US).format(visaFee))

        val processFee = product.processingFee.times(visaQuery.travellerCount)
        processingFeeValueText.set(NumberFormat.getNumberInstance(Locale.US).format(processFee))

        val courierCharge = product.courierCharge.times(visaQuery.travellerCount)
        courierChargeValueText.set(NumberFormat.getNumberInstance(Locale.US).format(courierCharge))

        total = visaFee + processFee + courierCharge
        totalAmount.set(NumberFormat.getNumberInstance(Locale.US).format(total))

        travellerTitle.set("Travelers x ${visaQuery.travellerCount}")
        visaFeeTitle.set("Visa Fee  x ${visaQuery.travellerCount}")
        courierTitle.set("Courier Charge x ${visaQuery.travellerCount}")

        val processingTitle = "Share Trip Processing  Fee x ${visaQuery.travellerCount}"
        processingFeeTitle.set(processingTitle)

        val totalDiscount = product.discountPrice?.times(visaQuery.travellerCount)
        totalPayable = total - totalDiscount!!

        updateTotalPayableWithConvenience()

        discount.set(NumberFormat.getNumberInstance(Locale.US).format(totalDiscount))

        lastSelected = lastSelectedPosition
        visaQuery.selectedVisaType = lastSelectedPosition
        visaQuery.totalAmount = totalPayable
        visaQuery.productCode = product.productCode
        visaQuery.visaType = product.type
    }

    private fun updateTotalPayableWithConvenience() {
        if (allPaymentMethod.value.isNullOrEmpty() || allPaymentMethod.value!![0].charge == 0.0) {
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

        totalPayableAmount.set(
            "BDT ${
                NumberFormat.getNumberInstance(Locale.US).format(ceil(totalPayable).toInt())
            }"
        )
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

    override fun onClickLogin() {
        isDismissDialog.value = true
        navigateLogin.value = Event(true)
    }

    companion object {
        const val search_visa = "search_visa"
        const val fetch_payment_gateway = "fetch_payment_gateway"
    }
}
