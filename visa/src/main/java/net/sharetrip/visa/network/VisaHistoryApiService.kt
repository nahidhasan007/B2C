package net.sharetrip.visa.network

import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.visa.history.model.*
import retrofit2.http.*

interface VisaHistoryApiService {

    @GET("api/v1/visa/booking/history")
    suspend fun fetchVisaBookingHistoryList(
        @Header("accesstoken") key: String, @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): RestResponse<VisaHistoryResponse>

    @GET("api/v1/visa/booking/detail")
    suspend fun fetchVisaBookingHistoryDetails(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String
    ): RestResponse<VisaHistoryDetailsResponse>

    @POST("api/v1/visa/booking/cancel")
    suspend fun cancelVisaBooking(
        @Header("accesstoken") key: String,
        @Body bookingCode: BookingID
    ): RestResponse<CancelVisaBookingResponse>

    @POST("api/v1/visa/booking/retryPayment")
    suspend fun retryPaymentVisaBooking(@Header("accesstoken") key: String, @Body bookingCode: BookingID):
            RestResponse<RetryPaymentVisaBookingResponse>

    @POST("api/v1/visa/booking/resendVoucher")
    suspend fun resendPaymentVisaBooking(@Header("accesstoken") key: String, @Body bookingCode: BookingID):
            RestResponse<ResendVoucharVisaBookingResponse>
}