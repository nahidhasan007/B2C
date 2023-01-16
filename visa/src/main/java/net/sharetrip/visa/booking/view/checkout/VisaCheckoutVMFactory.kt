package net.sharetrip.visa.booking.view.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.visa.booking.model.VisaSearchQuery

class VisaCheckoutVMFactory(
    private val visaSearchQuery: VisaSearchQuery
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VisaCheckoutViewModel(
            visaSearchQuery
        ) as T
    }
}
