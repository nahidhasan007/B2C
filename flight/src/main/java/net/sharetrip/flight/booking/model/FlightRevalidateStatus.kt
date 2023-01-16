package net.sharetrip.flight.booking.model

enum class FlightRevalidateStatus(val value: String) {

    SUCCESS("SUCCESS"),
    PRICE_CHANGE("PRICE_CHANGE"),
    ITINERARY_CHANGE("ITINERARY_CHANGE"),
    RE_ITINERARY_CHANGE ("RE-ITINERARY_CHANGE"),
    RE_VALIDATION_CHANGE("RE-VALIDATION_CHANGE")
}