package net.sharetrip.holiday.booking.view.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HolidayCitySearchViewModelFactory(
    private val rootPosition: Int,
    private val repository: HolidayCitySearchRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HolidayCitySearchViewModel::class.java))
            return HolidayCitySearchViewModel(rootPosition, repository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}