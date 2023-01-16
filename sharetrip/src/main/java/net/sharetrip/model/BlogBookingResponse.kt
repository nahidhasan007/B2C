package net.sharetrip.model

data class BlogBookingResponse(
	val bookings: List<BlogBookingItem> = ArrayList()
)