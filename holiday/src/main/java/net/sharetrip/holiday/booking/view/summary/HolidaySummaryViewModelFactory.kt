package net.sharetrip.holiday.booking.view.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.booking.model.HolidaySummary
import net.sharetrip.holiday.booking.model.PrimaryContact

class HolidaySummaryViewModelFactory(
    private val holidayParam: HolidayParam,
    private val contact: PrimaryContact,
    private val summary: HolidaySummary,
    private val tripCoin: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: HolidaySummaryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidaySummaryViewModel::class.java))
            return HolidaySummaryViewModel(
                holidayParam,
                contact,
                summary,
                tripCoin,
                sharedPrefsHelper,
                repository
            ) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}