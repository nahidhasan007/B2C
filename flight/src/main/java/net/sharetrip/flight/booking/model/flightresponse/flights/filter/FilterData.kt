package net.sharetrip.flight.booking.model.flightresponse.flights.filter

import net.sharetrip.flight.booking.model.filter.FilterParams

data class FilterData (
    var searchId: String = "",
    var page: Int = 0,
    var filter: FilterParams? = null
)
