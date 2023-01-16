package net.sharetrip.profile.model

import net.sharetrip.shared.utils.DateUtil

data class RewardWinner(
        var note: String,
        var winingDate: String,
        var activity: String,
        var name: String,
        var expireDate: String,
        var avatar: String,
        var type: String,
        var value: Int = 0
) {
    fun getFormattedExpireDate() : String {
        return DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(expireDate)
    }
}
