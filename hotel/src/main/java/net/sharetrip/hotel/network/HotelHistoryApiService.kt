package net.sharetrip.hotel.network

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.hotel.history.model.BookingVoucher
import net.sharetrip.hotel.history.model.CancelResponse
import net.sharetrip.hotel.history.model.HotelHistoryListResponse
import net.sharetrip.hotel.history.model.RetryPaymentHotelResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface HotelHistoryApiService {
    @GET("api/v1/hotel/booking-history")
    suspend fun getHotelHistoryListResponse(
        @Header("accesstoken") key: String, @Query("limit") limit: Int,
        @Query("offset") offset: Int, @Query("filterKey") filterKey: String,
    ): GenericResponse<RestResponse<HotelHistoryListResponse>>

    @POST("api/v1/hotel/cancel-booking")
    suspend fun cancelHotelBooking(
        @Header("accesstoken") key: String,
        @Body voucherId: BookingVoucher,
    ): GenericResponse<RestResponse<CancelResponse>>

    @GET("api/v1/hotel/booking/payment-link")
    suspend fun retryHotelPayment(
        @Header("accesstoken") key: String,
        @Query("voucherId") bookingCode: String,
    ): GenericResponse<RestResponse<RetryPaymentHotelResponse>>

    @GET("api/v1/hotel/resend-voucher")
    suspend fun resendHotelVoucher(
        @Header("accesstoken") key: String,
        @Query("voucherId") bookingCode: String,
    ): GenericResponse<ResponseBody>
}
