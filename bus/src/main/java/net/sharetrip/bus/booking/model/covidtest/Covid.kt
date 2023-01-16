package net.sharetrip.bus.booking.model.covidtest

data class Covid(
    val code: String = "",
    val optionsCode: String = "",
    val address: String = "",
    var self: Boolean = true
)
