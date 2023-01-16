package net.sharetrip.flight.booking.model

data class CouponListResponse(
        val name: String = "TKSCBC",
        val details: String = "Save 15% using SC card on Turkish Airlines Business",
        var isSelected: Boolean = true)