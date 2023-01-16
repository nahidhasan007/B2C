package net.sharetrip.profile.view.reward

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class RewardViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: RewardRepository
) : BaseViewModel() {

    val isDataLoading = repository.isDataLoading

    val isDataEmpty = Transformations.map(repository.leaderBoardData) {
        if (it.tripCoinWinners.isEmpty() && it.rewardWinners.isEmpty()) {
            MutableLiveData(true)
        } else
            MutableLiveData(false)
    }

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

    val openShareDialog = MutableLiveData<Event<Boolean>>()

    init {
        fetchLeaderBoardInfo()
    }

    fun onClickInviteFriend() {
        openShareDialog.value = Event(true)
    }

    private fun fetchLeaderBoardInfo() {
        val token = sharedPrefsHelper.get(PrefKey.ACCESS_TOKEN, "")
        viewModelScope.launch {
            repository.getWheelReward(token)
        }
    }

}
