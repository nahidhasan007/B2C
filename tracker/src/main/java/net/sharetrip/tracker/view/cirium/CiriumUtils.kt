package net.sharetrip.tracker.view.cirium

import net.sharetrip.shared.utils.DateFormatPattern
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.tracker.model.Delays
import net.sharetrip.tracker.model.FlightStatusData
import net.sharetrip.tracker.view.cirium.model.FlightStatus

object CiriumUtils {

    fun dateFindOut(flightStatus: FlightStatusData): Pair<String, String> {
        val departureDate = when (flightStatus.status) {
            FlightStatus.Active.status -> {
                flightStatus.operationalTimes.actualRunwayDeparture?.dateLocal
                    ?: flightStatus.operationalTimes.scheduledGateDeparture.dateLocal
            }

            FlightStatus.Landed.status -> {
                flightStatus.operationalTimes.actualRunwayDeparture?.dateLocal
                    ?: flightStatus.operationalTimes.scheduledGateDeparture.dateLocal
            }

            else -> flightStatus.operationalTimes.scheduledGateDeparture.dateLocal
        }

        val arrivalDate = when (flightStatus.status) {
            FlightStatus.Active.status -> {
                flightStatus.operationalTimes.actualRunwayArrival?.dateLocal
                    ?: flightStatus.operationalTimes.scheduledGateArrival.dateLocal
            }

            FlightStatus.Landed.status -> {
                flightStatus.operationalTimes.actualRunwayArrival?.dateLocal
                    ?: flightStatus.operationalTimes.scheduledGateArrival.dateLocal
            }

            else -> flightStatus.operationalTimes.scheduledGateArrival.dateLocal
        }

        val departureDateInFormat = DateUtil.revampingDateFormatFromCurrentToGiven(
            departureDate,
            DateFormatPattern.CIRIUM_API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_DATE_TIME_PATTERN
        )
        val arrivalDateInFormat = DateUtil.revampingDateFormatFromCurrentToGiven(
            arrivalDate,
            DateFormatPattern.CIRIUM_API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_DATE_TIME_PATTERN
        )
        return Pair(departureDateInFormat, arrivalDateInFormat)
    }

    fun delayFindOut(delays: Delays?): Pair<String, String> {
        var departureDelay = "--"
        var arrivalDelay = "--"

        if (delays != null) {
            val delayInDeparture =
                delays.departureGateDelayMinutes + delays.departureRunwayDelayMinutes
            departureDelay = if (delayInDeparture == 0) {
                "On Time"
            } else {
                "$delayInDeparture Minutes"
            }

            val delayInArrival = delays.arrivalGateDelayMinutes + delays.arrivalRunwayDelayMinutes
            arrivalDelay = if (delayInArrival == 0) {
                "On Time"
            } else {
                "$delayInArrival Minutes"
            }
        }

        return Pair(departureDelay, arrivalDelay)
    }

    fun flightStatusName(status: String?): String {
        return when (status) {
            FlightStatus.Active.status -> FlightStatus.Active.name
            FlightStatus.Scheduled.status -> FlightStatus.Scheduled.name
            FlightStatus.Landed.status -> FlightStatus.Landed.name
            FlightStatus.Canceled.status -> FlightStatus.Canceled.name
            FlightStatus.Redirected.status -> FlightStatus.Redirected.name
            FlightStatus.Diverted.status -> FlightStatus.Diverted.name
            FlightStatus.DataSourceNeeded.status -> FlightStatus.DataSourceNeeded.name
            FlightStatus.NoOperation.status -> FlightStatus.NoOperation.name
            FlightStatus.Unknown.status -> FlightStatus.Unknown.name
            else -> "--"
        }
    }
}
