package net.sharetrip.holiday.booking.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class HolidayHeaderViewModelFactory(private val holidayMainRepository: HolidayMainRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayHeaderViewModel::class.java))
            return HolidayHeaderViewModel(holidayMainRepository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}