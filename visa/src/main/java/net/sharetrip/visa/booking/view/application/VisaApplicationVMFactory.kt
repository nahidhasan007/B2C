package net.sharetrip.visa.booking.view.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.network.VisaBookingApiService

class VisaApplicationVMFactory(
    private val visaQuery: VisaSearchQuery,
    private val apiService: VisaBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VisaApplicationViewModel(
            visaQuery,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
