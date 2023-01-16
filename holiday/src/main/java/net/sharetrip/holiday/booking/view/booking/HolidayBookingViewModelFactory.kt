package net.sharetrip.holiday.booking.view.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.holiday.booking.model.HolidayParam

class HolidayBookingViewModelFactory(
    private val holidayParam: HolidayParam,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val holidayBookingRepository: HolidayBookingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayBookingViewModel::class.java))
            return HolidayBookingViewModel(holidayParam, sharedPrefsHelper, holidayBookingRepository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}