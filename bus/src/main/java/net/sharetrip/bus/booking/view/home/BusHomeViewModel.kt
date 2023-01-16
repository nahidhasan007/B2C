package net.sharetrip.bus.booking.view.home

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.DateUtil
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.bus.booking.model.Station

class BusHomeViewModel(private val repository: BusHomeRepository) : BaseViewModel() {

    private val _navigateToBusSearch = MutableLiveData<Event<Pair<String, Boolean>>>()

    val navigateToBusSearch: LiveData<Event<Pair<String, Boolean>>>
        get() = _navigateToBusSearch
    val originName = ObservableField<String>()
    val destinationName = ObservableField<String>()
    val destinationCode = ObservableField<String>()
    val departureDate = ObservableField<String>()
    val originStationCode = ObservableField<String>()
    val showMsg = MutableLiveData<String>()
    val apiStatus: LiveData<Status> = repository.apiStatus

    val listOfStations: LiveData<List<Station>> = repository.listOfStation

    private val _navigateToBusList = MutableLiveData<Event<Boolean>>()
    val navigateToBusList: LiveData<Event<Boolean>>
    get() = _navigateToBusList

    init {
        departureDate.set(DateUtil.todayApiDateFormat)
        getStationList()
    }

    fun onClickedFrom() {
        _navigateToBusSearch.value = Event(Pair("From", true))
    }

    fun onClickedTo() {
        if (!originStationCode.get().isNullOrEmpty()) {
            _navigateToBusSearch.value = Event(Pair("To", true))
            originStationCode.get()?.let {
            }
        } else
            showMsg.value = "Please select a origin station first"
    }

    fun swapStations() {
        if (destinationName.get().isNullOrEmpty()) {
            showMsg.value = "Please select a destination first."
        } else {
            var temp = destinationName.get()
            destinationName.set(originName.get())
            originName.set(temp)
            temp = destinationCode.get()
            destinationCode.set(originStationCode.get())
            originStationCode.set(temp)
        }
    }

    fun onClickBusSearch() {
        if (destinationName.get().isNullOrEmpty())
            showMsg.value = "Please select a destination first."
        else
            _navigateToBusList.value = Event(true)
    }

    fun getStationList() {
        viewModelScope.launch {
            repository.getStationList()
            if (apiStatus.value == Status.SUCCESS) {
                listOfStations.value?.forEach {
                    if (it.name.equals("DHAKA", true)) {
                        originName.set(it.name)
                        originStationCode.set(it.code)
                    }
                }
            }
        }
    }
}
