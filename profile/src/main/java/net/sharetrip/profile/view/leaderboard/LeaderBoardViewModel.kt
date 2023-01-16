package net.sharetrip.profile.view.leaderboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class LeaderBoardViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: LeaderBoardRepository
) : BaseViewModel() {

    val isDataLoading = repository.isDataLoading

    val tripCoinLoading = Transformations.map(repository.leaderBoardData) {
        if (it.tripCoinWinners.isNotEmpty()) {
            MutableLiveData(false)
        } else
            MutableLiveData(true)
    }

    val rewardLoading = Transformations.map(repository.leaderBoardData) {
        if (it.rewardWinners.isNotEmpty())
            MutableLiveData(false)
        else
            MutableLiveData(true)
    }

    val tripCoinWinnerList = Transformations.map(repository.leaderBoardData) {
        if (it.tripCoinWinners.isNotEmpty()) {
            it.tripCoinWinners
        } else {
            listOf()
        }
    }

    val rewardWinnerList = Transformations.map(repository.leaderBoardData) {
        if (it.rewardWinners.isNotEmpty()) {
            it.rewardWinners
        } else {
            listOf()
        }
    }

    init {
        fetchLeaderBoardInfo()
    }

    private fun fetchLeaderBoardInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        viewModelScope.launch {
            repository.getLeaderBoardInfo(token)
        }
    }

}
