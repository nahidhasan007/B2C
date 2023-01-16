package net.sharetrip.model

data class Deal(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val publishDate: String?,
    val platform: List<String>,
    val timeStamp: Long,
    val triggeredBy: String
)
