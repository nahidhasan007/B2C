package net.sharetrip.flight.booking.model.flightresponse.flights.segment

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.flightresponse.flights.flight.Airlines
import net.sharetrip.flight.booking.model.flightresponse.flights.flight.DateTime
import net.sharetrip.flight.booking.model.flightresponse.flights.flight.FlightAddress

@Parcelize
data class Segment(
    @Json(name = "providerCode")
    var providerCode: String = "",
    @Json(name = "searchId")
    var searchId: String = "",
    @Json(name = "sequenceNumber")
    var sequenceNumber: Int = 0,
    @Json(name = "sequenceCode")
    var sequenceCode: String = "",
    @Json(name = "airlines")
    var airlines: Airlines? = null,
    @Json(name = "airlinesCode")
    var airlinesCode: String = "",
    @Json(name = "flightNumber")
    var flightNumber: String = "",
    @Json(name = "resBookDesignCode")
    var resBookDesignCode: String = "",
    @Json(name = "logo")
    var logo: String = "",
    @Json(name = "aircraft")
    var aircraft: String = "",
    @Json(name = "aircraftCode")
    var aircraftCode: String = "",
    @Json(name = "operatedBy")
    var operatedBy: String = "",
    @Json(name = "marketingAirline")
    var marketingAirline: String = "",
    @Json(name = "originCode")
    var originCode: String = "",
    @Json(name = "originName")
    var originName: FlightAddress? = null,
    @Json(name = "originTerminal")
    var originTerminal: String = "",
    @Json(name = "destinationCode")
    var destinationCode: String = "",
    @Json(name = "destinationName")
    var destinationName: FlightAddress? = null,
    @Json(name = "destinationTerminal")
    var destinationTerminal: String = "",
    @Json(name = "arrivalDateTime")
    var arrivalDateTime: DateTime? = null,
    @Json(name = "departureDateTime")
    var departureDateTime: DateTime? = null,
    @Json(name = "departureTimeZone")
    var departureTimeZone: String = "",
    @Json(name = "arrivalTimeZone")
    var arrivalTimeZone: String = "",
    @Json(name = "duration")
    var duration: String = "",
    @Json(name = "marriageGrp")
    var marriageGrp: String = "",
    @Json(name = "baggageWeight")
    var baggageWeight: Int = 0,
    @Json(name = "baggageUnit")
    var baggageUnit: String = "",
    @Json(name = "infantBaggageWeight")
    var infantBaggageWeight: Int = 0,
    @Json(name = "infantBaggageUnit")
    var infantBaggageUnit: String = "",
    @Json(name = "fareReference")
    var fareReference: String = "",
    @Json(name = "seatsRemaining")
    var seatsRemaining: Int = 0,
    @Json(name = "seatsBelowMin")
    var seatsBelowMin: String = "",
    @Json(name = "cabin")
    var cabin: String = "",
    var cabinCode : String = "",
    var resBookDesigCode: String = "",
    @Json(name = "mealCode")
    var mealCode: String = "",
    @Json(name = "extraParams")
    var extraParams: ExtraParams? = null,
    @Json(name = "dayCount")
    var dayCount: Int = 0,
    @Json(name = "transitTime")
    var transitTime: String? = null,
    var classType: String = "",
    @Json(name = "hiddenStop")
    var hiddenStop: HiddenStop? = null

) : Parcelable {

    fun transitText(): Int {
        if (destinationCode == "HKG" && isNextCalenderDay())
            return R.string.TRANSIT_THAILAND_MORE_THAN_12_HOURS

        if (transitTime == null) return 0

        val times = transitTime?.split(" ")
        val hours = times?.get(0)?.replace("h", "")?.toInt()

        if (hours!! >= 24)
            return R.string.TRANSIT_MORE_THAN_24_HOURS

        if (hours >= 12 && destinationCode == "BKK" || destinationCode == "DMK")
            return R.string.TRANSIT_THAILAND_MORE_THAN_12_HOURS

        return 0
    }

    private fun isNextCalenderDay(): Boolean {
        val arrivalTime = arrivalDateTime?.time?.split(":")
        val hour = arrivalTime?.get(0)
        val time = arrivalTime?.get(1)

        val transit = transitTime?.split(" ")
        val addedHour = transit?.get(0)?.replace("h", "")
        val addedTime = transit?.get(1)?.replace("m", "")

        var totalHour = 0

        if (hour != null && time != null && addedHour != null && addedTime != null) {
            if ((time.toInt() + addedTime.toInt()) >= 60)
                totalHour += 1

            totalHour += hour.toInt().plus(addedHour.toInt())
        }

        if (totalHour >= 24)
            return true

        return false
    }
}
