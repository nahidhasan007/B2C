package net.sharetrip.tour.booking.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TourCitySearchVMF(private val repo: TourCitySearchRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourCitySearchViewModel::class.java))
            return TourCitySearchViewModel(repo) as T

        throw IllegalAccessException("Unknown ViewModel Class")
    }
}
