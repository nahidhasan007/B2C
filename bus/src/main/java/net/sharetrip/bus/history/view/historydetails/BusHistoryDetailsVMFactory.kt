package net.sharetrip.bus.history.view.historydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.bus.history.model.HistoryDetails

class BusHistoryDetailsVMFactory(private val historyDetails: HistoryDetails) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusHistoryDetailsViewModel::class.java))
            return BusHistoryDetailsViewModel(historyDetails) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
