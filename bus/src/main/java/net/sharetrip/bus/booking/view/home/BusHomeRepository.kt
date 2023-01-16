package net.sharetrip.bus.booking.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.Status
import net.sharetrip.bus.booking.model.Station
import net.sharetrip.bus.network.BusBookingApiService

class BusHomeRepository(private val apiService: BusBookingApiService) {

    private val _listOfStation = MutableLiveData<List<Station>>()
    val listOfStation: LiveData<List<Station>>
        get() = _listOfStation

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status>
    get() = _apiStatus

    suspend fun getStationList() {
        try {
            val data = apiService.getStation()
            _listOfStation.value = data.response!!
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            _apiStatus.value = Status.FAILED
        }
    }
}
