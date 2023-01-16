package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json
import net.sharetrip.flight.booking.model.Price
import java.util.*

data class FlightFilter (
    var cabin: List<Cabin>,
    var price: Price,
    @Json(name = "isRefundable")
    var isRefundable: List<Refundable>,
    var refundableCustom: List<Refundable>,
    var stop: List<Stop>,
    var airlines: List<Airline>,
    var origin: List<OriginAirport>,
    var destination: List<OriginAirport>,
    var layover: List<Layover>,
    var weight: List<Weight>,
    var outbound: List<Outbound>,
    @field:Json(name = "return")
    private var _return: List<Return>
) {
    fun get_return(): List<Return> {
        val bound: List<String> = ArrayList()
        return _return
    }

    fun set_return(_return: List<Return>) {
        this._return = _return
    }
}
