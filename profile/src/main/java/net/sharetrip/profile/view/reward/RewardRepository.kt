package net.sharetrip.profile.view.reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.LeaderBoardResponse
import net.sharetrip.profile.network.ProfileApiService

class RewardRepository(private val apiService: ProfileApiService) : BaseRepository() {

    private val _leaderBoardData = MutableLiveData<LeaderBoardResponse>()
    val leaderBoardData: LiveData<LeaderBoardResponse>
        get() = _leaderBoardData

    suspend fun getWheelReward(token: String) {
        callApi<LeaderBoardResponse>(
            executableCodeBlock = {
                apiService.fetchWheelReward(token)
            },

            onSuccess = {
                _leaderBoardData.value = it
            },

            onFailed = { _, _ -> })
    }
}
