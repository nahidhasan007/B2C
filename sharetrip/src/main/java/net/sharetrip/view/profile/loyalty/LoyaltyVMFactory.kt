package net.sharetrip.view.profile.loyalty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class LoyaltyVMFactory(val apiService: MainApiService, val sharedPrefsHelper: SharedPrefsHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoyaltyViewModel::class.java))
            return LoyaltyViewModel(apiService, sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}