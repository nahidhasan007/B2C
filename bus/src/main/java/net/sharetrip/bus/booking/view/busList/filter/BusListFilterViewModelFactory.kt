package net.sharetrip.bus.booking.view.busList.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.bus.booking.model.FilterData
import java.lang.IllegalArgumentException

class BusListFilterViewModelFactory(private var filterDataOld: FilterData): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(BusListFilterViewModel::class.java))
            return BusListFilterViewModel(filterDataOld) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
