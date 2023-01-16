package net.sharetrip.visa.booking.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.visa.network.VisaBookingApiService

class VisaHomeViewModelFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val apiService: VisaBookingApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisaHomeViewModel::class.java))
            return VisaHomeViewModel(sharedPrefsHelper, apiService) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
