package net.sharetrip.tour.booking.reserve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.tour.model.PeriodX
import net.sharetrip.tour.model.TourParam

class TourReserveVMF(private val tourOffer: PeriodX, private val tourParam: TourParam) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourReserveViewModel::class.java))
            return TourReserveViewModel(tourOffer, tourParam) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
