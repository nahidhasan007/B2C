package net.sharetrip.tracker.network

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.tracker.model.FlightStatusModel
import net.sharetrip.tracker.model.FlightTrackingResponse
import net.sharetrip.tracker.model.TravelAdviceResponse
import retrofit2.http.*

interface FlightTrackerApiService {

    @GET("https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/track/{flightNumber}")
    suspend fun getLiveFlightTrackingInfo(
        @Path("flightNumber") flightId: Int,
        @Query("appId") appId: String = "a80c4c0b",
        @Query("appKey") appKey: String = "41533e89e3e1e78a8f58e98e6c752130",
        @Query("includeFlightPlan") includeFlightPlan: Boolean = false,
        @Query("maxPositions") maxPositions: Int = 1
    ): GenericResponse<RestResponse<FlightTrackingResponse>>

    @GET("https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/status/{carrierCode}/{flightNumber}/dep/{year}/{month}/{day}")
    suspend fun fetchFlightStatusData(
        @Path("carrierCode") carrierCode: String,
        @Path("flightNumber") flightNumber: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String,
        @Query("appId") appId: String = "a80c4c0b",
        @Query("appKey") appKey: String = "41533e89e3e1e78a8f58e98e6c752130",
        @Query("utc") utc: Boolean = false
    ): GenericResponse<FlightStatusModel>

    @GET("https://api.traveladviceapi.com/search/{country_code}")
    suspend fun getTravelInfo(
        @Header("X-Access-Token") token: String,
        @Path("country_code") country_code: String
    ): GenericResponse<TravelAdviceResponse>

}
