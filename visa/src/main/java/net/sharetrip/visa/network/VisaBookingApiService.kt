package net.sharetrip.visa.network

import net.sharetrip.shared.model.EmptyResponse
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.visa.booking.model.*
import net.sharetrip.visa.booking.model.coupon.CouponRequest
import net.sharetrip.visa.booking.model.coupon.CouponResponse
import net.sharetrip.visa.booking.model.payment.PaymentMethod
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface VisaBookingApiService {

    @GET("api/v1/visa/popular")
    suspend fun fetchVisaCountryList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("nationality") nationality: String,
    ): GenericResponse<RestResponse<VisaListResponse>>

    @GET("api/v1/visa/country/popular")
    suspend fun fetchVisaPopularCountry(): GenericResponse<RestResponse<List<VisaCountry>>>

    @GET("api/v1/visa/country/search")
    suspend fun fetchVisaCountrySearch(@Query("keyword") keyword: String): GenericResponse<RestResponse<List<VisaCountry>>>

    @GET("api/v1/visa/search")
    suspend fun searchVisaTypeForACountry(
        @Query("visaCountryCode") visaCountryCode: String,
        @Query("entryDate") entryDate: String,
        @Query("exitDate") exitDate: String,
        @Query("travellers") travellers: Int,
        @Query("nationality") nationality: String
    ): GenericResponse<RestResponse<List<VisaSelection>>>

    @Multipart
    @POST("api/v1/visa/booking/upload")
    suspend fun uploadVisaDocuments(
        @Header("accesstoken") key: String,
        @Part uploadFile: MultipartBody.Part
    ): GenericResponse<RestResponse<VisaPhotoUploadRespone>>

    @Headers("Content-Type: application/json")
    @POST("api/v1/visa/booking")
    suspend fun bookAVisaRequest(
        @Header("accesstoken") key: String,
        @Body string: RequestBody
    ): GenericResponse<RestResponse<VisaBookingResponse>>

    @Headers("Content-Type: application/json")
    @PATCH("api/v1/visa/updateSearchRecord")
    suspend fun updateSearchRecord(
        @Header("accesstoken") key: String,
        @Body string: RequestBody
    ): GenericResponse<RestResponse<EmptyResponse>>

    @GET("api/v1/user/user-info")
    suspend fun getProfileResponse(@Header("accesstoken") key: String): GenericResponse<RestResponse<UserProfile>>


    @GET("api/v1/country")
    suspend fun getNationalityCodeList(@Header("accesstoken") key: String): GenericResponse<RestResponse<List<NationalityCode>>>

    @POST("/api/v1/coupon/validate")
    suspend fun getValidateCoupon(
        @Header("accesstoken") key: String,
        @Body couponRequest: CouponRequest
    ): GenericResponse<RestResponse<CouponResponse>>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGateway(
        @Query("service") service: String,
        @Query("currency") currency: String = "ALL"
    ): GenericResponse<RestResponse<List<PaymentMethod>>>
}
