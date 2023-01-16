package net.sharetrip.hotel.network

import net.sharetrip.shared.model.EmptyResponse
import net.sharetrip.shared.model.user.User
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.hotel.booking.model.*
import net.sharetrip.hotel.booking.model.coupon.*
import net.sharetrip.hotel.booking.model.payment.PaymentMethod
import net.sharetrip.hotel.history.model.BookingVoucher
import net.sharetrip.hotel.history.model.CancelResponse
import net.sharetrip.hotel.history.model.HotelHistoryListResponse
import net.sharetrip.hotel.history.model.RetryPaymentHotelResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface HotelBookingApiService {
    @GET("api/v1/hotel/search")
    suspend fun getHotelSearchResponse(@QueryMap queries: Map<String, String>): GenericResponse<RestResponse<HotelResponse>>

    @GET("api/v1/travel-review/cities")
    suspend fun getTravelReviewCities(@Header("accesstoken") key: String): GenericResponse<RestResponse<WannaGoResponse>>

    @POST("api/v1/travel-review/decision")
    suspend fun submitReviewDecision(
        @Header("accesstoken") key: String,
        @Body decision: ReviewDecision,
    ): GenericResponse<RestResponse<Any>>

    @GET("api/v1/flight/promotion")
    suspend fun getFlightPromotionBanner(): GenericResponse<RestResponse<FlightPromotion>>

    @GET("api/v1/hotel/search/property")
    suspend fun frequentHotelPropertyResponse(): GenericResponse<RestResponse<HotelNamesResponse>>

    @GET("api/v1/hotel/search/property")
    suspend fun getHotelPropertyResponse(@Query("keyword") keyword: String): GenericResponse<RestResponse<HotelNamesResponse>>

    @GET("api/v1/hotel/detail")
    suspend fun getHotelDetailResponse(
        @Query("searchCode") searchCode: String,
        @Query("hotelId") hotelId: String,
    ): GenericResponse<RestResponse<HotelDetailResponse>>

    @GET("api/v1/hotel/rooms")
    suspend fun getRoomListResponse(
        @Query("bundle") value: Boolean,
        @Query("searchCode") searchCode: String,
        @Query("hotelId") hotelId: String,
    ): GenericResponse<RestResponse<RoomListResponse>>

    @POST("api/v1/hotel/booking")
    suspend fun getHotelBookingResponse(
        @Header("accesstoken") key: String,
        @Body params: HotelBookingParams,
    ): GenericResponse<RestResponse<RoomBookingResponse>>

    @GET("api/v1/user/user-info")
    suspend fun getUserInformation(@Header("accesstoken") key: String): GenericResponse<RestResponse<User>>

    @GET("api/v1/flight/booking/booking-cancel")
    suspend fun cancelFlight(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String,
    ): GenericResponse<ResponseBody>

    @GET("api/v1/flight/voucher-send")
    suspend fun resendVoucher(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String,
    ): GenericResponse<ResponseBody>

    @GET("api/v1/hotel/booking-history")
    suspend fun getHotelHistoryListResponse(
        @Header("accesstoken") key: String, @Query("limit") limit: Int,
        @Query("offset") offset: Int, @Query("filterKey") filterKey: String,
    )
            : GenericResponse<RestResponse<HotelHistoryListResponse>>

    @POST("api/v1/hotel/cancel-booking")
    suspend fun cancelHotelBooking(
        @Header("accesstoken") key: String,
        @Body voucherId: BookingVoucher,
    )
            : GenericResponse<RestResponse<CancelResponse>>

    @GET("api/v1/hotel/booking/payment-link")
    suspend fun retryHotelPayment(
        @Header("accesstoken") key: String,
        @Query("voucherId") bookingCode: String,
    ):
            GenericResponse<RestResponse<RetryPaymentHotelResponse>>

    @GET("api/v1/hotel/resend-voucher")
    suspend fun resendHotelVoucher(
        @Header("accesstoken") key: String,
        @Query("voucherId") bookingCode: String,
    ): GenericResponse<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/get-share-link")
    suspend fun getShareResponse(
        @Header("accesstoken") key: String, @Field("type") type: String,
        @Field("value") value: String,
    ): GenericResponse<RestResponse<String>>

    @POST("/api/v1/coupon/validate")
    suspend fun getValidateHotelCoupon(
        @Header("accesstoken") key: String,
        @Body couponRequest: HotelCouponRequest,
    ): GenericResponse<RestResponse<CouponResponse>>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGateway(@Query("service") service: String): GenericResponse<RestResponse<List<PaymentMethod>>>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGateway(
        @Query("service") service: String,
        @Query("currency") currency: String = "ALL",
    ): GenericResponse<RestResponse<List<PaymentMethod>>>

    @GET("api/v1/user/user-info")
    suspend fun getProfileResponse(@Header("accesstoken") key: String): GenericResponse<RestResponse<UserProfile>>

    @GET("api/v1/country")
    suspend fun getNationalityCodeList(@Header("accesstoken") key: String): GenericResponse<RestResponse<List<NationalityCode>>>

    @GET("api/v1/user/user-info")
    suspend fun getPrimaryContactResponse(@Header("accesstoken") key: String): GenericResponse<RestResponse<PrimaryContact>>

    @GET("https://notifier.sharetrip.net/api/v1/coupon?service=hotel")
    suspend fun getHotelCouponList(): GenericResponse<RestResponse<List<HotelCoupon>>>

    @POST("/api/v1/loyalty-check")
    suspend fun gpLoyaltyCheck(
        @Header("accesstoken") key: String,
        @Body gpLoyaltyCheckRequest: GpLoyaltyCheckRequest
    ): GenericResponse<RestResponse<GPLoyaltyCheckResponse>>

    @POST("api/v1/otp-verify")
    suspend fun verifyOTP(
        @Header("accesstoken") key: String,
        @Body verifyOTPRequest: VerifyOTPRequest
    ): GenericResponse<RestResponse<EmptyResponse>>
}
