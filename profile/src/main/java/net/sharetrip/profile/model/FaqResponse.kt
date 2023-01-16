package net.sharetrip.profile.model

data class FaqResponse (
    val common_overview: FaqItem?,
    val hotel: FaqItem?,
    val flight: FaqItem?,
    val holiday: FaqItem?,
    val tour: FaqItem?,
    val transfer: FaqItem?,
    val trip_coin : FaqItem?
)
