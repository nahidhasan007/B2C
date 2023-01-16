package net.sharetrip.holiday.booking.view.reserve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.holiday.booking.model.HolidayOffer
import net.sharetrip.holiday.booking.model.HolidayParam
import java.lang.IllegalArgumentException

class HolidayReserveViewModelFactory(
    private val holidayParam: HolidayParam,
    private val holidayOffer: HolidayOffer
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayReserveViewModel::class.java))
            return HolidayReserveViewModel(holidayParam, holidayOffer) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
