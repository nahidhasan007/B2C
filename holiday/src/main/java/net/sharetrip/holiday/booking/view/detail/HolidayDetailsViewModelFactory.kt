package net.sharetrip.holiday.booking.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class HolidayDetailsViewModelFactory(
    private val productCode: String,
    private val isInternetAvailable: Boolean,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val holidayDetailsRepository: HolidayDetailsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayDetailViewModel::class.java))
            return HolidayDetailViewModel(
                productCode,
                isInternetAvailable,
                sharedPrefsHelper,
                holidayDetailsRepository
            ) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}