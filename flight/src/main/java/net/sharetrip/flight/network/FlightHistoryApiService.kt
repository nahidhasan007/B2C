package net.sharetrip.flight.network

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.flight.history.model.*
import okhttp3.ResponseBody
import retrofit2.http.*
import retrofit2.http.Header

interface FlightHistoryApiService {
    @GET("api/v1/flight/booking/history?status=all")
    suspend fun getFlightHistoryResponse(
        @Header("accesstoken") key: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): RestResponse<FlightHistoryData>

    @GET("api/v1/flight/booking/booking-cancel")
    suspend fun cancelFlight(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String
    ): GenericResponse<ResponseBody>

    @GET("api/v1/flight/voucher-send")
    suspend fun resendVoucher(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String
    ): GenericResponse<ResponseBody>

    @GET("api/v1/flight/booking/payment-link")
    suspend fun retryPayment(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String
    ): GenericResponse<RestResponse<Any>>

    @GET("api/v1/flight/search/fare-rules")
    suspend fun getAirFareRules(
        @Query("searchId") mSessionId: String,
        @Query("sequenceCode") mSequenceId: String
    ): GenericResponse<RestResponse<AirFareResponseOfRule>>

    @GET("api/v1/flight/refund/eligible-travellers")
    suspend fun getRefundEligibleTravellers(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String
    ): GenericResponse<RestResponse<RefundEligibleTravellersResponse>>

    @GET("api/v1/flight/refund/quotation")
    suspend fun refundQuotation(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String,
        @Query("eTickets") eTickets: String
    ): GenericResponse<RestResponse<RefundQuotationResponse>>

    @POST("api/v1/flight/refund/confirm")
    suspend fun confirmRefundRequest(
        @Header("accesstoken") key: String,
        @Body refundSearchId: RefundConfirmBody
    ): GenericResponse<RestResponse<Any>>

    @GET("api/v1/flight/void/quotation")
    suspend fun voidQuotation(
        @Header("accesstoken") key: String,
        @Query("bookingCode") bookingCode: String,
        @Query("searchId") searchId: String
    ): GenericResponse<RestResponse<VoidQuotationResponse>>

    @POST("api/v1/flight/void/confirm")
    suspend fun confirmVoidRequest(
        @Header("accesstoken") key: String,
        @Body voidSearchIdBody: VoidSearchIdBody
    ): GenericResponse<RestResponse<Any>>
}
