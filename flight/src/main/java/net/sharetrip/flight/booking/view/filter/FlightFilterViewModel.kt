package net.sharetrip.flight.booking.view.filter

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.booking.model.Price
import net.sharetrip.flight.booking.model.filter.FilterParams

class FlightFilterViewModel : BaseViewModel() {
    val totalFlight = ObservableField<String>()
    val liveData = MutableLiveData<@FilterTypeEnum Int>()
    var stopList = ArrayList<String>()
    var airlineList = ArrayList<String>()
    var layoverList = ArrayList<String>()
    var weightList = ArrayList<String>()
    var onwardTimeList = ArrayList<String>()
    var returnTimeList = ArrayList<String>()
    var filterRefundable: Int? = null
    var filterPrice = Price(0f, 0f)
    var onApplyClicked = MutableLiveData<Boolean>()
    var filterParams: FilterParams? = null
    var isRefundableList = ArrayList<Int>()
    val onClickReset = MutableLiveData<Boolean>()

    fun onClickPriceIcon() {
        liveData.value = PRICE
    }

    fun onClickRefundableIcon() {
        liveData.value = REFUNDABLE
    }

    fun onClickStopIcon() {
        liveData.value = STOPS
    }

    fun onClickAirlineIcon() {
        liveData.value = AIRLINE
    }

    fun onClickLayoverIcon() {
        liveData.value = LAYOVER
    }

    fun onClickWeightIcon() {
        liveData.value = WEIGHT
    }

    fun onClickOnwardTimeIcon() {
        liveData.value = TIME
    }

    fun onClickFilterReset() {
        onClickReset.value = true
    }

    fun onClickFilterSearchButton() {
        filterParams = FilterParams()
        if (onClickReset.value != true)
            filterParams?.apply {
                price = filterPrice
                airlines = airlineList.ifEmpty { null }
                layover = layoverList.ifEmpty { null }
                stops = stopList.ifEmpty { null }
                weight = weightList.ifEmpty { null }
                refundable = filterRefundable

                if (isRefundableList.size > 0) isRefundable = isRefundableList
                if (onwardTimeList.size > 0) outbound = onwardTimeList[0]
                if (returnTimeList.size > 0) returnTime = returnTimeList[0]
            }
        onApplyClicked.value = true
    }

    fun setPriceRange(minValue: Float?, maxValue: Float?) {
        filterPrice = Price(maxValue!!, minValue!!)
    }
}
