package net.sharetrip.flight.booking.model.coupon

data class HotelExtraParams(val providerCode: String,
                            val propertyCode: String,
                            val rooms: MutableList<Int>)