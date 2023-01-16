package net.sharetrip.tour.history.pricing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.tour.model.TourHistoryItem
import java.lang.IllegalArgumentException

class TourPricingVMF(private val historyItem: TourHistoryItem): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourPricingViewModel::class.java))
            return TourPricingViewModel(historyItem) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
