package net.sharetrip.profile.view.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.LeaderBoardResponse
import net.sharetrip.profile.network.ProfileApiService

class LeaderBoardRepository(private val apiService: ProfileApiService) : BaseRepository() {

    private val _leaderBoardData = MutableLiveData<LeaderBoardResponse>()
    val leaderBoardData: LiveData<LeaderBoardResponse>
        get() = _leaderBoardData

    suspend fun getLeaderBoardInfo(token: String) {
        callApi<LeaderBoardResponse>(
            executableCodeBlock = {
                apiService.getLeaderBoardData(token)
            },

            onSuccess = {
                _leaderBoardData.value = it
            },

            onFailed = { _, _ -> }
        )
    }
}