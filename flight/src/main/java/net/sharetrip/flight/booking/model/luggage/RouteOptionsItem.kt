package net.sharetrip.flight.booking.model.luggage

data class RouteOptionsItem(
    val origin: String,
    val destination: String,
    val options: List<OptionsItem>,
    var travellerBaggageList: MutableList<TravellerBaggage> = ArrayList(),
    var selectedItem: Int = -1,
    var selectedCode: String? = "",
    var isExpanded: Boolean = false
) {
    val onlyAdultOptionList: List<OptionsItem>
        get() = options.filter { it.travellerType == TravellerType.ADT.toString() }
}
