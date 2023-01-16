package net.sharetrip.profile.model

data class SavedCards(
    val customer: String,
    val details: Details,
    val number: String,
    val platform: String,
    val tokenExpiry: String,
    val uid: String
)
