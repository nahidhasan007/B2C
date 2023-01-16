package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json
import java.util.*

data class Schedule (
    @Json(name = "outbound")
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
