package net.sharetrip.tracker.view.cirium.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tracker.view.cirium.CiriumUtils
import net.sharetrip.tracker.view.cirium.CiriumUtils.flightStatusName
import net.sharetrip.tracker.view.cirium.model.FlightTrackingDataHolder
import net.sharetrip.tracker.view.cirium.model.StatusInfo

class FlightStatusListViewModel : BaseViewModel(), OnItemClickListener {

    val statusList: LiveData<List<StatusInfo>>
        get() = _statusList
    private val _statusList = MutableLiveData<List<StatusInfo>>()

    private val _navigateToFlightStatusDetailsScreen = MutableLiveData<Event<Boolean>>()
    val navigateToFlightStatusDetailsScreen: LiveData<Event<Boolean>>
        get() = _navigateToFlightStatusDetailsScreen

    var trackingDataHolder = FlightTrackingDataHolder()

    init {
        generateStatusList()
    }

    private fun generateStatusList() {
        val dataList = ArrayList<StatusInfo>()

        trackingDataHolder.flightStatusDataList?.let { list ->
            list.forEach { it ->
                val carrierInfo = trackingDataHolder.carrierInfo
                val airlineIcon =
                    "https://tbbd-flight.s3.ap-southeast-1.amazonaws.com/airlines-logo/" + carrierInfo.code + ".png"

                val airlineList = trackingDataHolder.airlines ?: ArrayList()

                val airlineName = airlineList.filter { it.iata == carrierInfo.code }[0].name!!
                val airlineNumber = carrierInfo.code + carrierInfo.number

                val (departureDate, arrivalDate) = CiriumUtils.dateFindOut(it)

                val departureAirportCode = it.departureAirportFsCode ?: ""
                val arrivalAirportCode = it.arrivalAirportFsCode ?: ""

                val departureTime = departureDate.substring(0, 7)
                val arrivalTime = arrivalDate.substring(0, 7)
                val status = flightStatusName(it.status)

                val info = StatusInfo(
                    airlineName, airlineNumber, airlineIcon, departureTime,
                    departureAirportCode, arrivalTime, arrivalAirportCode, status
                )

                dataList.add(info)
            }
        }
        _statusList.value = dataList
    }

    override fun onItemClicked(position: Int) {
        trackingDataHolder.flightStatusData = trackingDataHolder.flightStatusDataList?.get(position)
        _navigateToFlightStatusDetailsScreen.value = Event(true)
    }
}

interface OnItemClickListener {
    fun onItemClicked(position: Int)
}