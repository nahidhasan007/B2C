package net.sharetrip.holiday.booking.view.summary

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.ErrorResponse
import net.sharetrip.shared.model.coupon.CouponRequest
import net.sharetrip.shared.model.coupon.CouponResponse
import net.sharetrip.payment.model.PaymentMethod
import com.sharetrip.base.network.model.GatewayService
import com.sharetrip.base.network.model.Status
import net.sharetrip.holiday.booking.model.HolidayBookingParam
import net.sharetrip.holiday.booking.model.HolidayBookingResponse
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.network.HolidayBookingApiService
import retrofit2.HttpException

class HolidaySummaryRepository(private val apiService: HolidayBookingApiService) {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status>
        get() = _apiStatus

    private val _dataListPack = MutableLiveData<ArrayList<PaymentMethod>>()
    val dataListPack: LiveData<ArrayList<PaymentMethod>>
        get() = _dataListPack

    private val _isCouponShow = ObservableBoolean(true)
    val isCouponShow: ObservableBoolean
        get() = _isCouponShow

    private val _showMsg = MutableLiveData<Event<String>>()
    val showMsg: LiveData<Event<String>>
    get() = _showMsg

    private val _gatewayList = MutableLiveData<List<String>>()
    val gatewayList: LiveData<List<String>>
    get() = _gatewayList

    private val _couponResponse = MutableLiveData<CouponResponse>()
    val couponResponse: LiveData<CouponResponse>
    get() = _couponResponse

    private var _holidayBookingResponse = HolidayBookingResponse()
    val holidayBookingResponse:HolidayBookingResponse
    get() = _holidayBookingResponse

    val paymentList = MutableLiveData<List<PaymentMethod>>()


    suspend fun getPaymentGateways(param: HolidayParam) {
        _dataLoading.value = true
        _apiStatus.value = Status.RUNNING
        try {
            val data = apiService.fetchPaymentGateway(
                GatewayService.Package.name,
                param.gatewayCurrency, param.bankGatewayList
            )
            _dataListPack.value = data.response as ArrayList<PaymentMethod>
            paymentList.value = data.response.filter { it.isEarnTripCoinApplicable }
            _isCouponShow.set(data.response.any { it.isCouponAvailable })
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            _apiStatus.value = Status.FAILED
        }
        _dataLoading.value = false
    }

    suspend fun getValidateCoupon(key: String, couponRequest: CouponRequest){
        _dataLoading.value = true
        _apiStatus.value = Status.RUNNING
        try {
            val data = apiService.getValidateCoupon(key,couponRequest)
            _couponResponse.value = data.response
            _apiStatus.value = Status.SUCCESS

        } catch (e: Exception) {
            handleError(e)
        }
        _dataLoading.value = false
    }

    suspend fun getHolidayBookingResponse(token: String, holidayParam: HolidayBookingParam) {
        _dataLoading.value = true
        _apiStatus.value = Status.RUNNING
        try {
            val data = apiService.fetchHolidayBookingResponse(token, holidayParam)
            _holidayBookingResponse = data.response
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            handleError(e)
            _apiStatus.value = Status.FAILED
        }
        _dataLoading.value = false
    }

    private fun handleError(throwable: Throwable) {
        when (throwable) {
            is HttpException -> {
                val errorBody = throwable.response()?.errorBody()!!.string()
                val errorResponse: ErrorResponse =
                    Gson().fromJson(errorBody, ErrorResponse::class.java)
                _showMsg.value = Event(errorResponse.message)
            }
            else -> {
                _showMsg.value = Event("Something went wrong")
            }
        }
    }
}