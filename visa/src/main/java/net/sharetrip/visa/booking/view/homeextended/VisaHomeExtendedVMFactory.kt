package net.sharetrip.visa.booking.view.homeextended

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.visa.booking.model.VisaSearchQuery

class VisaHomeExtendedVMFactory(
    private val visaSearchQuery: VisaSearchQuery
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VisaHomeExtendedViewModel(
            visaSearchQuery
        ) as T
    }
}
