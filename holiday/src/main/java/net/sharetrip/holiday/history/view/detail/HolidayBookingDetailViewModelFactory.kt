package net.sharetrip.holiday.history.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.holiday.network.HolidayBookingApiService
import java.lang.IllegalArgumentException

class HolidayBookingDetailViewModelFactory(
    private val bookingCode: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: HolidayBookingDetailRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayBookingDetailViewModel::class.java))
            return HolidayBookingDetailViewModel(bookingCode, sharedPrefsHelper, repository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
