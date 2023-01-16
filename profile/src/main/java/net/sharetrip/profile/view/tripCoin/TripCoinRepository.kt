package net.sharetrip.profile.view.tripCoin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.ErrorType
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.TripCoinSummaryResponse
import net.sharetrip.profile.network.ProfileApiService

class TripCoinRepository(private val apiService: ProfileApiService) : BaseRepository() {

    private val _tripCoinSummaryResponse = MutableLiveData<TripCoinSummaryResponse>()
    val tripCoinSummaryResponse: LiveData<TripCoinSummaryResponse>
        get() = _tripCoinSummaryResponse

    suspend fun getTripCoinData(token: String) {
        callApi<TripCoinSummaryResponse>(
            executableCodeBlock = {
                apiService.getTripSummary(token)
            },

            onSuccess = {
                _tripCoinSummaryResponse.value = it
            },

            onFailed = { errorType, errorMessage ->
                if (errorType == ErrorType.API_ERROR) {
                    showMessage.value = Event(errorMessage)
                } else {
                    showMessage.value = Event("An Error Occurred")
                }
            }
        )
    }
}