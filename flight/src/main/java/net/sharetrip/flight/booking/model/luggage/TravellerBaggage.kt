package net.sharetrip.flight.booking.model.luggage

data class TravellerBaggage(
    val title: String,
    val optionList: List<OptionsItem>,
    val travellerType: TravellerType,
    val isLuggageOptional: Boolean,
    var selectedItem: Int = -1,
    var selectedCode: String = "",
    var isExpanded: Boolean = false)