package net.sharetrip.tour.history.cancellationpolicy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TourCancellationPolicyVMF(private val htmlString: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourCancellationPolicyVM::class.java))
            return TourCancellationPolicyVM(htmlString) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
