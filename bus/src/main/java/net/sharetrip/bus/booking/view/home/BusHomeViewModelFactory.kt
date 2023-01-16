package net.sharetrip.bus.booking.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BusHomeViewModelFactory(private val repository: BusHomeRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusHomeViewModel::class.java))
            return BusHomeViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
