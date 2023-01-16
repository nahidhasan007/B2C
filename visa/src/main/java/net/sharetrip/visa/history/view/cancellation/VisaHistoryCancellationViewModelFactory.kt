package net.sharetrip.visa.history.view.cancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class VisaHistoryCancellationViewModelFactory(private val data: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisaHistoryCancellationViewModel::class.java))
            return VisaHistoryCancellationViewModel(data) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}