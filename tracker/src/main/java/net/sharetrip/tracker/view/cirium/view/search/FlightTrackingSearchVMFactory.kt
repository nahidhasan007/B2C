package net.sharetrip.tracker.view.cirium.view.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FlightTrackingSearchVMFactory(private val repository: FlightTrackingSearchRepo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightTrackingSearchViewModel::class.java))
            return FlightTrackingSearchViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
