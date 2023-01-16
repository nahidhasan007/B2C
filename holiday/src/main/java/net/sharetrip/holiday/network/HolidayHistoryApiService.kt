package net.sharetrip.holiday.network

import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.holiday.history.model.BookingCode
import net.sharetrip.holiday.history.model.BookingHolidayDetails
import net.sharetrip.holiday.history.model.HolidayHistoryResponse
import retrofit2.http.*

interface HolidayHistoryApiService {

    @GET("api/v1/package/booking/detail")
    suspend fun getHolidayBookingDetails(
        @Header("accessToken") token: String,
        @Query("bookingCode") bookingCode: String
    ): RestResponse<BookingHolidayDetails>

    @POST("api/package/booking/cancel")
    suspend fun cancelHolidayBooking(
        @Header("accesstoken") key: String,
        @Body bookingCode: BookingCode
    ): RestResponse<net.sharetrip.holiday.history.model.CancelResponse>

    @GET("api/v1/package/booking/history")
    suspend fun getHolidayHistoryResponse(
        @Header("accesstoken") key: String, @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): RestResponse<HolidayHistoryResponse>
}
