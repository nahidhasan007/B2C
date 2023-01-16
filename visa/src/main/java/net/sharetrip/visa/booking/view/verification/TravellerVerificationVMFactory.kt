package net.sharetrip.visa.booking.view.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.visa.booking.model.VisaSearchQuery

class TravellerVerificationVMFactory(
    private val visaSearchQuery: VisaSearchQuery
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TravelerVerificationViewModel(
            visaSearchQuery
        ) as T
    }
}
