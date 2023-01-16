package net.sharetrip.visa.history.view.price_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.visa.history.model.VisaProductSnapshot
import java.lang.IllegalArgumentException

class VisaHistoryPriceDetailsViewModelFactory(
    private val product: VisaProductSnapshot,
    val travellerCount: Int,
    private val convenienceFee: Int,
    private val vatOnConvenienceFee: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisaHistoryPriceDetailsViewModel::class.java))
            return VisaHistoryPriceDetailsViewModel(
                product,
                travellerCount,
                convenienceFee,
                vatOnConvenienceFee
            ) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}