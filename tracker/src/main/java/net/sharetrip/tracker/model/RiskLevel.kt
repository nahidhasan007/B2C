package net.sharetrip.tracker.model

enum class RiskLevel(val value: String) {
    PROHIBITED("red"),
    PERMITTED("green"),
    MODERATE("amber"),
    UNKNOWN("unknown"),
}
