package net.sharetrip.bus.network

import net.sharetrip.shared.model.EmptyResponse
import net.sharetrip.shared.model.user.User
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.booking.model.payment.PaymentMethod
import retrofit2.http.*
import retrofit2.http.Header

interface BusBookingApiService {

    @GET("api/v1/bus/stations")
    suspend fun getStation(): RestResponse<List<Station>>

    @GET("api/v1/bus/routes")
    suspend fun getRoutes(@Query("sourceStationCode") sourceStationCode: String): RestResponse<RouteDetails>

    @POST("api/v1/bus/search")
    suspend fun getBus(@Body busSearch: BusSearch): GenericResponse<RestResponse<CoachList>>

    @POST("api/v1/bus/booking")
    suspend fun postBookingRequest(
        @Header("accessToken") token: String,
        @Body busBooking: BookingRequest
    ): GenericResponse<RestResponse<Any>>

    @POST("api/v1/bus/cart/update")
    suspend fun selectSeat(@Body select: SelectSeatRequest): GenericResponse<RestResponse<SelectSeatRequestResponse>>

    @POST("api/v1/bus/cart/lock")
    suspend fun cartLock(@Body select: RequestLockCart): GenericResponse<RestResponse<CartLockResponse>>

    @POST("api/v1/bus/details")
    suspend fun getCoachDetails(@Body coach: CoachDetails): GenericResponse<RestResponse<Departure>>

    @GET("api/v1/user/user-info")
    suspend fun getUserInformation(@Header("accesstoken") key: String): GenericResponse<RestResponse<User>>

    @POST("api/v1/travel-details/add-new-traveler")
    suspend fun addTraveler(@Header("accesstoken") key: String, @Body addTravelerParam: Traveler):
            GenericResponse<RestResponse<EmptyResponse>>

    @GET("api/v1/user/user-info")
    suspend fun getProfileResponse(@Header("accesstoken") key: String): GenericResponse<RestResponse<UserProfile>>

    @GET("api/v1/payment/gateWay")
    suspend fun fetchPaymentGateway(
        @Query("service") service: String,
        @Query("currency") currency: String = "ALL", @Query("bankCode") code: List<String>?
    ): GenericResponse<RestResponse<List<PaymentMethod>>>
}
