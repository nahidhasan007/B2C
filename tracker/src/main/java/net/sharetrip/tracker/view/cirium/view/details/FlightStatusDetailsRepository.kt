package net.sharetrip.tracker.view.cirium.view.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.tracker.base.BaseRepository
import net.sharetrip.tracker.network.FlightTrackerApiService
import net.sharetrip.tracker.view.cirium.model.FlightPosition
import net.sharetrip.tracker.view.cirium.model.FlightTrackingResponse

class FlightStatusDetailsRepository(private val apiService: FlightTrackerApiService) :
    BaseRepository() {

    private val _flightPosition = MutableLiveData<FlightPosition>()
    val flightPosition: LiveData<FlightPosition>
        get() = _flightPosition

    suspend fun getFlightTrackingInfo(flightId: Int) {
        callApi<FlightTrackingResponse?>(
            executableCodeBlock = {
                apiService.getLiveFlightTrackingInfo(flightId)
            },

            onSuccess = {
                it?.flightTrack?.let { liveTrackInfo ->
                    if (!liveTrackInfo.positions.isNullOrEmpty())
                        _flightPosition.value = liveTrackInfo.positions[0]
                }
            },

            onFailed = { _, errorMessage ->
                showMessage.value = errorMessage
            }
        )
    }
}
