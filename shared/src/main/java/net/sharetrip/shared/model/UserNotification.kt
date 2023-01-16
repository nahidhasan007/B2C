package net.sharetrip.shared.model

data class UserNotification (
    val comment: String?,
    val description: String?,
    val imageUrl: String?,
    val platform: String?,
    val tigerBy: String?,
    val publishDate: String?,
    val timeStamp: Long?,
    val title: String?,
    var fromPushNotification: Boolean = false
)
