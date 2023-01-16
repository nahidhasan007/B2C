package net.sharetrip.holiday.history.model

data class HolidayHistoryResponse(
    var data: List<HolidayHistoryItem> = ArrayList(),
    var offset: Int = 0,
    var limit: Int = 0,
    var count: Int = 0
)
