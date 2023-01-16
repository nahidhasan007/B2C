package net.sharetrip.profile.view.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class LeaderBoardVMFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: LeaderBoardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderBoardViewModel::class.java))
            return LeaderBoardViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}