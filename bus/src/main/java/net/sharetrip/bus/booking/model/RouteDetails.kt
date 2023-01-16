package net.sharetrip.bus.booking.model

data class RouteDetails(
    val code: String,
    val dataSource: String,
    val route: List<Route>
)
