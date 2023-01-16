package net.sharetrip.tour.network

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.tour.model.TourHistoryItem
import net.sharetrip.tour.model.TourHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TourHistoryAPIService {

    @GET("api/v1/tour/booking/history")
    suspend fun getTourHistoryListResponse(
        @Header("accesstoken") key: String, @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): RestResponse<TourHistoryResponse>

    @GET("api/v1/tour/booking/detail")
    suspend fun getTourBookingDetail(
        @Header("accesstoken") key: String, @Query("bookingCode") bookingCode: String
    ): GenericResponse<RestResponse<TourHistoryItem>>
}
