package net.sharetrip.tracker.view.cirium.view.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.tracker.view.cirium.CiriumUtils.dateFindOut
import net.sharetrip.tracker.view.cirium.CiriumUtils.delayFindOut
import net.sharetrip.tracker.view.cirium.CiriumUtils.flightStatusName
import net.sharetrip.tracker.view.cirium.model.FlightPosition
import net.sharetrip.tracker.view.cirium.model.FlightStatus
import net.sharetrip.tracker.view.cirium.model.FlightTrackingDataHolder
import net.sharetrip.tracker.view.cirium.model.StatusDetails

class FlightStatusDetailsViewModel(
    private val trackingDataHolder: FlightTrackingDataHolder,
    private val repository: FlightStatusDetailsRepository
) : BaseViewModel() {

    val airlineIcon: LiveData<String>
        get() = _airlineIcon
    private val _airlineIcon = MutableLiveData<String>()

    val statusInfo: LiveData<StatusDetails>
        get() = _statusInfo
    private val _statusInfo = MutableLiveData<StatusDetails>()

    val position: LiveData<FlightPosition> = repository.flightPosition

    val showToast = repository.showMessage

    init {
        dataFindOut()
    }

    private fun dataFindOut() {
        val flightStatus = trackingDataHolder.flightStatusData!!
        val carrierInfo = trackingDataHolder.carrierInfo
        _airlineIcon.value =
            "https://tbbd-flight.s3.ap-southeast-1.amazonaws.com/airlines-logo/" + carrierInfo.code + ".png"

        val airportList = trackingDataHolder.airports
        val airlineList = trackingDataHolder.airlines

        if (!airportList.isNullOrEmpty() && !airlineList.isNullOrEmpty()) {

            val airPathCode =
                flightStatus.departureAirportFsCode + " - " + flightStatus.arrivalAirportFsCode
            val airlineName = airlineList.filter { it.iata == carrierInfo.code }[0].name!!
            val airlineNumber = carrierInfo.code + carrierInfo.number

            val departureTerminal = flightStatus.airportResources?.departureTerminal ?: "Not Found"
            val departureGate = flightStatus.airportResources?.departureGate ?: "Not Found"
            val departureAirport =
                airportList.filter { it.fs == flightStatus.departureAirportFsCode }[0]
            val departureAirportName = departureAirport.name + ", " + departureAirport.countryName
            val departureTimeZone = when {
                departureAirport.utcOffsetHours > 0 -> " (GMT +" + departureAirport.utcOffsetHours + ")"
                else -> " (GMT " + departureAirport.utcOffsetHours + ")"
            }

            val arrivalTerminal = flightStatus.airportResources?.arrivalTerminal ?: "Not Found"
            val arrivalGate = flightStatus.airportResources?.arrivalGate ?: "Not Found"
            val arrivalAirport =
                airportList.filter { it.fs == flightStatus.arrivalAirportFsCode }[0]
            val arrivalAirportName = arrivalAirport.name + ", " + arrivalAirport.countryName
            val arrivalTimeZone = when {
                arrivalAirport.utcOffsetHours > 0 -> " (GMT +" + arrivalAirport.utcOffsetHours + ")"
                else -> " (GMT " + arrivalAirport.utcOffsetHours + ")"
            }

            val (departureDate, arrivalDate) = dateFindOut(flightStatus)

            val (departureDelay, arrivalDelay) = delayFindOut(flightStatus.delays)

            val status = flightStatusName(flightStatus.status)

            if (FlightStatus.Active.status == flightStatus.status)
                fetchTrackingInfo()

            val statusInfo = StatusDetails(
                airPathCode,
                airlineName,
                airlineNumber,
                departureDate + departureTimeZone,
                departureTerminal,
                departureGate,
                departureAirportName,
                departureDelay,
                arrivalDate + arrivalTimeZone,
                arrivalTerminal,
                arrivalGate,
                arrivalAirportName,
                arrivalDelay,
                status
            )
            _statusInfo.value = statusInfo
        }
    }

    private fun fetchTrackingInfo() {
        viewModelScope.launch {
            trackingDataHolder.flightStatusData?.flightId?.let { repository.getFlightTrackingInfo(it) }
        }
    }
}
