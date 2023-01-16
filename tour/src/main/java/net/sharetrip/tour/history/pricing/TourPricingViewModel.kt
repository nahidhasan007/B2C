package net.sharetrip.tour.history.pricing

import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.model.TourHistoryItem

class TourPricingViewModel(historyItem: TourHistoryItem) : BaseViewModel() {

    var historyItem: TourHistoryItem

    init {
        this.historyItem = historyItem
    }

}
