package net.sharetrip.bus.history.view.historydetails

import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.utils.SingleLiveEvent

class BusHistoryDetailsViewModel(val busHistoryData: HistoryDetails) : BaseViewModel() {
    var isOneway = MutableLiveData(false)
    val isClicked = MutableLiveData<Boolean>()
    var pos: Int = 0
    val navigateToPassengerDetails = SingleLiveEvent<Any>()
    val navigateToPricingDetails = SingleLiveEvent<Any>()
    val navigateToPolicies = SingleLiveEvent<Any>()

    init {
        val seats = mutableListOf<String>()

        busHistoryData.seats.forEach {
            seats.add(it.seatNo[0] + "-" + it.seatNo[1])
        }

        busHistoryData.seatsNo = seats.joinToString()
    }

    fun gotoPassengerDetails() {
        navigateToPassengerDetails.call()
    }

    fun gotoPricingDetails() {
        navigateToPricingDetails.call()
    }

    fun gotoPolicies() {
        navigateToPolicies.call()
    }
}
