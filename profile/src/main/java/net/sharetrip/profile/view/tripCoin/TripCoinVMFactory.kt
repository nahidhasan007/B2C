package net.sharetrip.profile.view.tripCoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class TripCoinVMFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: TripCoinRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripCoinViewModel::class.java))
            return TripCoinViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
