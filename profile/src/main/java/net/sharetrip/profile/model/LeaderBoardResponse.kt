package net.sharetrip.profile.model

import com.squareup.moshi.Json

data class LeaderBoardResponse (
    @field:Json(name = "special")
    var rewardWinners: List<RewardWinner>,
    @field:Json(name = "tripCoin")
    var tripCoinWinners: List<TripCoinWinner>
)
