package net.sharetrip.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class HomeActionsVMFactory(
    private val apiService: MainApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeActionsViewModel::class.java))
            return HomeActionsViewModel(
                apiService = apiService,
                sharedPrefsHelper = sharedPrefsHelper
            ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}