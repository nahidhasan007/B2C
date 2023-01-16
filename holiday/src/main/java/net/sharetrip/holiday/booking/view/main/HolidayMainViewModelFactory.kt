package net.sharetrip.holiday.booking.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HolidayMainViewModelFactory(
    private val repository: HolidayMainRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayMainViewModel::class.java))
            return HolidayMainViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
