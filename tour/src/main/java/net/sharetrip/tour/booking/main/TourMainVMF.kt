package net.sharetrip.tour.booking.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TourMainVMF(private val repository: TourMainRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourMainViewModel::class.java))
            return TourMainViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
