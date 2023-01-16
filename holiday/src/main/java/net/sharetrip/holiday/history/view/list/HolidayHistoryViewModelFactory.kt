package net.sharetrip.holiday.history.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class HolidayHistoryViewModelFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: HolidayHistoryListRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayHistoryViewModel::class.java))
            return HolidayHistoryViewModel(sharedPrefsHelper, repository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
