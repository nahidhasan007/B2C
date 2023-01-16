package net.sharetrip.bus.booking.view.busList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.bus.booking.model.BusSearch

class BusListViewModelFactory(
    private val busSearch: BusSearch,
    private val repository: BusListRepository,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusListViewModel::class.java))
            return BusListViewModel(busSearch, repository, sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
