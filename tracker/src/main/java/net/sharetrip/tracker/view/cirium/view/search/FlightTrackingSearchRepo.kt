package net.sharetrip.tracker.view.cirium.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.tracker.base.BaseRepository
import net.sharetrip.tracker.model.FlightStatusModel
import net.sharetrip.tracker.network.FlightTrackerApiService

class FlightTrackingSearchRepo(private val apiService: FlightTrackerApiService) : BaseRepository() {

    private val _flightStatusModel = MutableLiveData<FlightStatusModel>()
    val flightStatusModel: LiveData<FlightStatusModel>
        get() = _flightStatusModel

    suspend fun getFlightStatusData(
        carrierCode: String,
        flightNumber: String,
        year: String,
        month: String,
        day: String
    ) {
        callApi<FlightStatusModel>(
            executableCodeBlock = {
                apiService.fetchFlightStatusData(carrierCode, flightNumber, year, month, day)
            },

            onSuccess = {
                _flightStatusModel.value = it
            },

            onFailed = { _, _ -> }
        )
    }
}