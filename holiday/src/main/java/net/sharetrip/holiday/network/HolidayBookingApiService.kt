package net.sharetrip.holiday.network

import net.sharetrip.shared.model.coupon.CouponRequest
import net.sharetrip.shared.model.coupon.CouponResponse
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.shared.model.user.User
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.holiday.booking.model.*
import retrofit2.http.*

interface HolidayBookingApiService {
    @GET("api/v1/package/list")
    suspend fun fetchPopularHoliday(@Query("limit") limit: Int, @Query("offset") offset: Int)
            :RestResponse<HolidayListResponse>

    @GET("api/v1/package/city/popular")
    suspend fun fetchPopularCityForHoliday(): RestResponse<List<HolidayCity>>


    @GET("api/v1/package/list")
    suspend fun fetchCityHoliday(
        @Query("cityCodes") cityCode: String, @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): RestResponse<HolidayListResponse>

    @GET("api/v1/package/detail")
    suspend fun fetchHolidayDetailResponse(@Query("productCode") productCode: String)
            : RestResponse<HolidayDetailResponse>

    /*TBBD treasure api*/
    @FormUrlEncoded
    @POST("api/v1/get-share-link")
    suspend fun getShareResponse(
        @Header("accesstoken") key: String, @Field("type") type: String,
        @Field("value") value: String
    ): GenericResponse<RestResponse<String>>

    @GET("api/v1/user/user-info")
    suspend fun getUserInformation(@Header("accesstoken") key: String): RestResponse<User>

    @GET("api/v1/user/user-info")
    suspend fun getHolidayContactResponse(@Header("accesstoken") key: String): RestResponse<HolidayContact>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGateway(
        @Query("service") service: String,
        @Query("currency") currency: String = "ALL", @Query("bankCode") code: List<String>?
    ): RestResponse<List<PaymentMethod>>

    @POST("api/v1/package/booking")
    suspend fun fetchHolidayBookingResponse(
        @Header("accessToken") token: String,
        @Body bookingParam: HolidayBookingParam
    ): RestResponse<HolidayBookingResponse>

    @POST("/api/v1/coupon/validate")
    suspend fun getValidateCoupon(
        @Header("accesstoken") key: String,
        @Body couponRequest: CouponRequest
    ): RestResponse<CouponResponse>

    @GET("api/v1/package/city/search")
    suspend fun searchCityListForHoliday(@Query("keyword") key: String): RestResponse<List<HolidayCity>>
}
