package net.sharetrip.profile.view.quickpick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.Traveler
import net.sharetrip.profile.model.UserProfile
import net.sharetrip.profile.network.ProfileApiService

class QuickPickRepository(private val apiService: ProfileApiService) : BaseRepository() {

    private val _passengerList = MutableLiveData<List<Traveler>>()
    val passengerList: LiveData<List<Traveler>>
        get() = _passengerList

    suspend fun getProfileInfo(token: String) {
        callApi<UserProfile>(
            executableCodeBlock = { apiService.getProfileResponse(token) },

            onSuccess = {
                _passengerList.value = it.otherPassengers
            },

            onFailed = { _, _ -> }
        )
    }
}
