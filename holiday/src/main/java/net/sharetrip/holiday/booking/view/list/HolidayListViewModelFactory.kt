package net.sharetrip.holiday.booking.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HolidayListViewModelFactory(
    private val cityCode: String,
    private val repository: HolidayListRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayListViewModel::class.java))
            return HolidayListViewModel(cityCode, repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
