package net.sharetrip.tracker.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.tracker.base.BaseRepository
import net.sharetrip.tracker.model.TravelAdviceResponse
import net.sharetrip.tracker.network.FlightTrackerApiService

class TravelAdviceSearchRepo(private val apiService: FlightTrackerApiService) : BaseRepository() {

    private val _travelAdvice = MutableLiveData<TravelAdviceResponse>()
    val travelAdvice: LiveData<TravelAdviceResponse>
        get() = _travelAdvice

    suspend fun fetchTravelInfo(token: String, countryCode: String) {
        callApi<TravelAdviceResponse>(
            executableCodeBlock = {
                apiService.getTravelInfo(token, countryCode)
            },

            onSuccess = {
                _travelAdvice.value = it
            },

            onFailed = { _, _ ->
                showMessage.value = "No Data found"
            }
        )
    }

}
