package net.sharetrip.bus.booking.view.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class BusSearchViewModelFactory(
    private val originStationCode: String,
    private val repository: BusSearchRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusSearchViewModel::class.java))
            return BusSearchViewModel(originStationCode, repository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
