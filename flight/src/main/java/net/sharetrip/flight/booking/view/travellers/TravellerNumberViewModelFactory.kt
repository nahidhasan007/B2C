package net.sharetrip.flight.booking.view.travellers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.booking.model.TravellersInfo

class TravellerNumberViewModelFactory(val travellersInfo: TravellersInfo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravellerNumberViewModel::class.java))
            return TravellerNumberViewModel(
                travellersInfo
            ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
