package net.sharetrip.tour.booking.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.tour.model.TourParam
import java.lang.IllegalArgumentException

class PickUpLocationVMF(private val tourParam: TourParam): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PickupLocationViewModel::class.java))
            return PickupLocationViewModel(tourParam) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
