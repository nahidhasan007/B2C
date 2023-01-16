package net.sharetrip.visa.booking.view.nationality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.visa.network.VisaBookingApiService

class NationalityVMFactory(
    private val apiService: VisaBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NationalityViewModel(
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
