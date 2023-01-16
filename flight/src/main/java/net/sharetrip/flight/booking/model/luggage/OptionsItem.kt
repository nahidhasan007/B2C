package net.sharetrip.flight.booking.model.luggage

data class OptionsItem(
        val amount: Double = 0.0,
        val code: String = "",
        val quantity: Int = 0,
        val details: String = "",
        val currency: String = "",
        val travellerType: String = ""
)