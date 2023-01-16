package net.sharetrip.hotel.history.view.historylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class HotelHistoryViewModelFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: HotelHistoryListRepo,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelHistoryListViewModel::class.java))
            return HotelHistoryListViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
