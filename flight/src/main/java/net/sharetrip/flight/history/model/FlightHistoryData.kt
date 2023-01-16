package net.sharetrip.flight.history.model

data class FlightHistoryData(
    val data: List<FlightHistoryResponse>,
    val count: Int,
    val limit: Int,
    val offset: Int
)