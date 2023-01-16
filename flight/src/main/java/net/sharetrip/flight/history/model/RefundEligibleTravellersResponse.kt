package net.sharetrip.flight.history.model

data class RefundEligibleTravellersResponse(
    val bookingCode: String,
    val travellers: List<Traveller>
)
