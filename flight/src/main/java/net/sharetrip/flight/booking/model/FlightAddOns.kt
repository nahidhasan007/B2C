package net.sharetrip.flight.booking.model

data class FlightAddOns(
    val covid : List<CovidAddOnResponseItem>,
    val travelInsurance : List<TravelInsuranceItem>,
    val baggageInsurance : List<BaggageInsurance>
)