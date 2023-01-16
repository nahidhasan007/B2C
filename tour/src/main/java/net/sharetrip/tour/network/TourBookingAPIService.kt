package net.sharetrip.tour.network

import net.sharetrip.shared.model.coupon.CouponRequest
import net.sharetrip.shared.model.coupon.CouponResponse
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.tour.model.*
import retrofit2.http.*

interface TourBookingAPIService {

    @GET("api/v1/tour/list")
    suspend fun fetchTourList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("cityCode") cityCode: String? = null,
        @Query("filterBy") filterBy: String? = null
    ): GenericResponse<RestResponse<TourListResponse>>

    @GET("api/v1/tour/city/popular")
    suspend fun fetchPopularCityForTour(): GenericResponse<RestResponse<List<TourPopCity>>>

    @GET("api/v1/tour/detail")
    suspend fun fetchTourDetailResponse(@Query("productCode") tourCode: String): GenericResponse<RestResponse<TourDetailResponseNew>>

    @FormUrlEncoded
    @POST("api/v1/get-share-link")
    suspend fun getShareResponse(
        @Header("accesstoken") key: String, @Field("type") type: String,
        @Field("value") value: String
    ): GenericResponse<RestResponse<String>>

    @GET("api/v1/tour/city/search")
    suspend fun searchCityListForTour(@Query("keyword") key: String): GenericResponse<RestResponse<List<TourPopCity>>>

    @GET("api/v1/user/user-info")
    suspend fun getTourContactResponse(@Header("accesstoken") key: String): GenericResponse<RestResponse<TourContact>>

    @POST("/api/v1/coupon/validate")
    suspend fun getValidateCoupon(
        @Header("accesstoken") key: String,
        @Body couponRequest: CouponRequest
    ): GenericResponse<RestResponse<CouponResponse>>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGatewayRX(@Query("service") service: String): GenericResponse<RestResponse<List<net.sharetrip.payment.model.PaymentMethod>>>

    @POST("api/v1/tour/booking")
    suspend fun fetchTourBookingResponse(
        @Header("accessToken") token: String,
        @Body bookingParam: TourBookingParam
    ): GenericResponse<RestResponse<TourBookingResponse>>
}
