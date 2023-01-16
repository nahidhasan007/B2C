package net.sharetrip.bus.history.view.historylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.bus.network.BusHistoryApiService

class BusHistoryVMFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: BusHistoryListRepo,
    private val apiService: BusHistoryApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusHistoryViewModel::class.java))
            return BusHistoryViewModel(sharedPrefsHelper, repository, apiService) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
