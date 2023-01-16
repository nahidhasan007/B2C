package net.sharetrip.tour.booking.location

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.shared.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.booking.reserve.TourReserveFragment.Companion.ARG_TOUR_PARAM_MODEL
import net.sharetrip.tour.model.TourParam

class PickupLocationViewModel(private val tourParam: TourParam) : BaseViewModel() {

    private val _navigateToContact = MutableLiveData<Event<Bundle>>()
    val navigateToContact: LiveData<Event<Bundle>>
    get() = _navigateToContact

    val msg = MutableLiveData<String>()

    fun navigateToContact(pickupLocation: String, additionalRequirement: String, agree: Boolean) {
        tourParam.apply {
            this.pickUpLocation = pickupLocation
            additionalReq = additionalRequirement
        }

        if (tourParam.pickUpLocation.isNotEmpty() && agree && !additionalRequirement.isEmpty()) {
            _navigateToContact.value = Event(bundleOf(ARG_TOUR_PARAM_MODEL to tourParam))
        } else {
            msg.value = "Fill up all required info"
        }
    }

}
