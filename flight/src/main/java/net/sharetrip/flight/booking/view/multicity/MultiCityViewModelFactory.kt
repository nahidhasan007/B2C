package net.sharetrip.flight.booking.view.multicity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MultiCityViewModelFactory(
    private val searchQuery: String,
    private val stringForTo: String,
    private val stringForFrom: String,
    private val stringForDate: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultiCityViewModel::class.java))
            return MultiCityViewModel(
                searchQuery,
                stringForTo,
                stringForFrom,
                stringForDate
            ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}