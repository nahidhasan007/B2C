package net.sharetrip.profile.model

import java.util.*

data class TripCoinSummaryResponse (
    var availableCoins: Int = 0,
    var pendingCoin: Int = 0,
    var expiringSixtyDays: Int = 0,
    var allPoints: List<TripCoinItem> = ArrayList()
)
