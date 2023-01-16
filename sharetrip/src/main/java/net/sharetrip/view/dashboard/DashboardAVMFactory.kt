package net.sharetrip.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class DashboardAVMFactory(
    private val apiService: MainApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardActivityViewModel(
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
