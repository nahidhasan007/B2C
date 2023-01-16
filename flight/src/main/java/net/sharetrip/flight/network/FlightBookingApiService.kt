package net.sharetrip.flight.network

import net.sharetrip.shared.model.AdvanceSearchResponse
import net.sharetrip.shared.model.EmptyResponse
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.flight.booking.model.*
import net.sharetrip.flight.booking.model.coupon.*
import net.sharetrip.flight.booking.model.flightresponse.FlightsResponse
import net.sharetrip.flight.booking.model.flightresponse.flights.filter.FilterData
import net.sharetrip.flight.booking.model.luggage.LuggageResponse
import net.sharetrip.flight.booking.model.rulemodel.AirFareResponse
import net.sharetrip.flight.booking.model.user.User
import net.sharetrip.flight.booking.view.searchairport.data.Airport
import net.sharetrip.payment.model.PaymentMethod
import okhttp3.MultipartBody
import retrofit2.http.*

interface FlightBookingApiService {
    @GET("api/v1/flight/promotion")
    suspend fun getFlightPromotionBanner(): GenericResponse<RestResponse<FlightPromotion>>

    @GET("api/v1/flight/search/airport")
    suspend fun getAirports(@Query("name") mSearchText: String): GenericResponse<RestResponse<List<Airport>>>

    @POST("/api/v1/flight/search/filter")
    suspend fun getFlightFilter(@Body mFilter: FilterData): GenericResponse<RestResponse<FlightsResponse>>

    @GET("api/v1/flight/advance-search")
    suspend fun getFlightCalendarPriceInfo(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("depart") departOne: String?,
        @Query("depart") departTwo: String?
    ): GenericResponse<RestResponse<AdvanceSearchResponse>>

    @GET("api/v1/flight/search")
    suspend fun getFlights(
        @Query("tripType") mTripType: String, @Query("adult") mAdult: Int,
        @Query("child") mChild: Int, @Query("childAge[]") mChildDateOfBirthList: List<String>,
        @Query("infant") mInfant: Int,
        @Query("class") mClassType: String, @Query("origin") mOrigin: List<String>,
        @Query("destination") mDestination: List<String>,
        @Query("depart") mDepart: List<String>
    ): GenericResponse<RestResponse<FlightsResponse>>

    @GET("api/v1/flight/luggage")
    suspend fun getBaggageDetails(
        @Query("searchId") searchId: String,
        @Query("sessionId") sessionId: String,
        @Query("sequenceCode") sequenceCode: String
    ): GenericResponse<RestResponse<LuggageResponse>>

    @GET("api/v1/flight/search/fare-rules")
    suspend fun getAirFareRules(
        @Query("searchId") mSessionId: String,
        @Query("sequenceCode") mSequenceId: String
    ): GenericResponse<RestResponse<AirFareResponse>>

    @GET("https://notifier.sharetrip.net/api/v1/coupon?service=flight")
    suspend fun getCouponList(): GenericResponse<RestResponse<List<FlightCoupon>>>

    @GET("api/v1/user/user-info")
    suspend fun getUserInformation(@Header("accesstoken") key: String): GenericResponse<RestResponse<User>>

    @POST("/api/v1/coupon/validate")
    suspend fun getValidateFlightCoupon(
        @Header("accesstoken") key: String,
        @Body couponRequest: FlightCouponRequest
    ): GenericResponse<RestResponse<CouponResponse>>

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

    @GET("api/v1/user/user-info")
    suspend fun getProfileResponseRX(@Header("accesstoken") key: String): GenericResponse<RestResponse<UserProfile>>

    @GET("api/v1/country")
    suspend fun getNationalityCodeList(@Header("accesstoken") key: String): GenericResponse<RestResponse<List<NationalityCode>>>

    @Multipart
    @POST("api/v1/upload-file")
    suspend fun sendFile(
        @Header("accesstoken") key: String,
        @Part uploadFile: MultipartBody.Part
    ): GenericResponse<ImageUploadResponse>

    @GET("/api/v1/flight/ssr-codes")
    suspend fun getSsrCodeResponse(): GenericResponse<RestResponse<List<MealWheelchairResponse>>>

    @GET("api/v1/flight/addons/service")
    suspend fun getAddOns(@Query("searchId") searchId: String, @Query("sequenceCode") sequenceCode: String) : GenericResponse<RestResponse<FlightAddOns>>

    @GET("api/v1/flight/addons/service-details")
    suspend fun getAddOnsDetails(@Query("code") code: String, @Query("service") service: String) : GenericResponse<RestResponse<FlightAddOnsDetails>>

    @GET("api/v1/user/user-info")
    suspend fun getPrimaryContactResponse(@Header("accesstoken") key: String): GenericResponse<RestResponse<PrimaryContact>>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGatewayRX(@Query("service") service: String): GenericResponse<RestResponse<List<PaymentMethod>>>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGateway(
        @Query("service") service: String,
        @Query("currency") currency: String = "ALL", @Query("bankCode") code: List<String>?
    ): GenericResponse<RestResponse<List<PaymentMethod>>>

    @POST("api/v1/flight/booking/booking")
    suspend fun fetchFlightPaymentUrl(
        @Header("accesstoken") key: String,
        @Body mFlightBookingDetail: FlightBookingDetail
    ): GenericResponse<RestResponse<Any>>

    @POST("api/v1/flight/revalidate")
    suspend fun priceCheckBeforeFlightBooking(
        @Header("accesstoken") key: String,
        @Body priceCheckBody: PriceCheckBody
    ): GenericResponse<RestResponse<PriceCheckResponse>>

    @GET("api/v1/flight/details")
    suspend fun getFlightDetails(
        @Query("searchId") searchId: String,
        @Query("sessionId") sessionId: String,
        @Query("sequenceCode") sequenceCode: String
    ): GenericResponse<RestResponse<FlightDetailsResponse>>
}
