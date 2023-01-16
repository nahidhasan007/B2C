package net.sharetrip.tracker.view.cirium.view.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.tracker.view.cirium.model.FlightTrackingDataHolder

class FlightStatusDetailsVMFactory(
    private val trackingDataHolder: FlightTrackingDataHolder,
    private val repository: FlightStatusDetailsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightStatusDetailsViewModel::class.java))
            return FlightStatusDetailsViewModel(trackingDataHolder, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
