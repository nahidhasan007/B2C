package net.sharetrip.bus.network

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.bus.history.model.BusHistoryResponse
import net.sharetrip.bus.history.model.HistoryDetails
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BusHistoryApiService {

    @GET("api/v1/bus/booking/history")
    suspend fun getBookingHistoryList(
        @Header("accessToken") accessToken: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): GenericResponse<RestResponse<BusHistoryResponse>>

    @GET("api/v1/bus/booking/details")
    suspend fun getBookingHistoryDetails(
        @Query("bookingId") bookingId: String,
        @Header("accessToken") accessToken: String
    ): GenericResponse<RestResponse<HistoryDetails>>
}
