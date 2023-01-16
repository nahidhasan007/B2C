package net.sharetrip.profile.view.reward

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class RewardViewModelFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: RewardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RewardViewModel::class.java))
            return RewardViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
