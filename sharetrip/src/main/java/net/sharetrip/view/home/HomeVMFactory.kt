package net.sharetrip.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class HomeVMFactory(
    private val apiService: MainApiService,
    private val remoteConfig: FirebaseRemoteConfig,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(apiService, remoteConfig, sharedPrefsHelper) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}