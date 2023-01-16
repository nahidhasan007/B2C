package net.sharetrip.hotel.history.view.pricing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.hotel.history.model.HotelHistoryItem

class PricingInfoVMFactory(
    val historyItem: HotelHistoryItem
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PricingInfoViewModel::class.java))
            return PricingInfoViewModel(
                historyItem
            ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
