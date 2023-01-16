package net.sharetrip.flight.booking.view.searchairport

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.flight.booking.view.searchairport.data.Airport
import net.sharetrip.flight.booking.view.searchairport.data.AirportDao
import net.sharetrip.flight.network.FlightBookingApiService
import kotlin.concurrent.thread

class SearchAirportRepo(private val bookingApiService: FlightBookingApiService, private val airportDao: AirportDao) {

    suspend fun getAirports(searchText: String): GenericResponse<RestResponse<List<Airport>>> {
        return bookingApiService.getAirports(searchText)
    }

    fun insert(airport: Airport) {
        thread {
            try {
                airportDao.insert(airport)
            } catch (e : Exception) {

            }
        }
    }

    suspend fun getAirports() = airportDao.getAirports().asReversed()
}
