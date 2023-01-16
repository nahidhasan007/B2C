package net.sharetrip.bus.booking.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.bus.booking.model.Route
import net.sharetrip.bus.booking.model.Station
import net.sharetrip.bus.network.BusBookingApiService

class BusSearchRepository(private val apiService: BusBookingApiService) {

    private val _stationList = MutableLiveData<List<Station>>()
    val stationList : LiveData<List<Station>>
    get() = _stationList

    private val _routes = MutableLiveData<List<Route>>()
    val routes: LiveData<List<Route>>
    get() = _routes

    suspend fun getStationList(){
        try {
            val data = apiService.getStation()
            _stationList.value = data.response!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getRoutes(originStationCode: String){
        try {
            val data = apiService.getRoutes(originStationCode)
            _routes.value = data.response.route
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
