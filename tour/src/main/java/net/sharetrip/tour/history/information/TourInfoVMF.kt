package net.sharetrip.tour.history.information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.tour.model.TourHistoryItem
import java.lang.IllegalArgumentException

class TourInfoVMF(private val historyItem: TourHistoryItem): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourInfoViewModel::class.java))
            return TourInfoViewModel(historyItem) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
