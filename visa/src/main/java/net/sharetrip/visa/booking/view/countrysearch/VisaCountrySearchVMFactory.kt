package net.sharetrip.visa.booking.view.countrysearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.visa.network.VisaBookingApiService

class VisaCountrySearchVMFactory(
    private val apiService: VisaBookingApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VisaCountrySearchViewModel(
            apiService
        ) as T
    }
}
