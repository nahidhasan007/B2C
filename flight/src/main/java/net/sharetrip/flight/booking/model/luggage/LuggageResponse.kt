package net.sharetrip.flight.booking.model.luggage

data class LuggageResponse(
    val isLuggageOptional: Boolean,
    val wholeFlightOptions: List<OptionsItem>?,
    val routeOptions: List<RouteOptionsItem>?,
    val isPerPerson: Boolean,
    val wholeFlight: Boolean,
    var totalTraveller: Int,
    var travellerBaggageList: MutableList<TravellerBaggage> = ArrayList()
)
