package net.sharetrip.tracker.view.cirium.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.sharetrip.tracker.model.AirlineElement
import net.sharetrip.tracker.model.AirportInfoDetails
import net.sharetrip.tracker.model.FlightStatusData

@Parcelize
data class FlightTrackingDataHolder(
    var flightStatusData: FlightStatusData? = null,
    var airports: List<AirportInfoDetails>? = listOf(),
    var airlines: List<AirlineElement>? = listOf(),
    var carrierInfo: CarrierInfo = CarrierInfo(),
    var flightStatusDataList: List<FlightStatusData>? = listOf()
) : Parcelable
