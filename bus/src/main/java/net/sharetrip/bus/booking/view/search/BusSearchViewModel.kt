package net.sharetrip.bus.booking.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.bus.booking.model.Route
import net.sharetrip.bus.booking.model.Station

class BusSearchViewModel(
    val originStationCode: String,
    private val repository: BusSearchRepository
) : BaseViewModel() {
    val stationList: LiveData<List<Station>> = repository.stationList
    val routes: LiveData<List<Route>> = repository.routes

    init {
        if (originStationCode.isEmpty()) {
            getStationList()
        } else {
            getRoutes()
        }
    }

    fun getStationList() {
        viewModelScope.launch {
            repository.getStationList()
        }
    }

    fun getRoutes() {
        viewModelScope.launch {
            repository.getRoutes(originStationCode)
        }
    }

}
