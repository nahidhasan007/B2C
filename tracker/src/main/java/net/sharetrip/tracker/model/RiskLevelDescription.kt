package net.sharetrip.tracker.model

import net.sharetrip.tracker.R

class RiskLevelDescription(private val riskLevel: RiskLevel) {
    val levelMessage: String
        get() = when(riskLevel) {
            RiskLevel.PERMITTED -> "Risk Level: Green"
            RiskLevel.MODERATE -> "Risk Level: Amber"
            RiskLevel.PROHIBITED -> "Risk Level: Red"
            else -> "Risk Level: Amber"
        }

    val textColor: Int
        get() = when(riskLevel) {
            RiskLevel.PERMITTED -> R.color.mid_green
            RiskLevel.MODERATE -> R.color.mango
            RiskLevel.PROHIBITED -> R.color.red_800
            else -> R.color.mango
        }

    val backGroundColor: Int
        get() = when(riskLevel) {
            RiskLevel.PERMITTED -> R.color.light_green
            RiskLevel.MODERATE -> R.color.off_white
            RiskLevel.PROHIBITED -> R.color.very_light_pink
            else -> R.color.mango
        }

    val tintColor: Int
        get() = when(riskLevel) {
            RiskLevel.PERMITTED -> R.color.green_700
            RiskLevel.MODERATE -> R.color.mango
            RiskLevel.PROHIBITED -> R.color.red_800
            else -> R.color.mango
        }
}
