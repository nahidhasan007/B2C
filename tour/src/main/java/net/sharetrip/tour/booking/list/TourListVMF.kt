package net.sharetrip.tour.booking.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.tour.model.TourPopCity

class TourListVMF(private val city: TourPopCity, private val repo: TourListRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourListViewModel::class.java))
            return TourListViewModel(city, repo) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
