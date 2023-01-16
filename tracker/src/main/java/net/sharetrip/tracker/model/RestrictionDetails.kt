package net.sharetrip.tracker.model

import com.squareup.moshi.Json

data class RestrictionDetails(
    val level: Int? = null,
    val notes: List<NotesItem> = ArrayList(),
    @field:Json(name = "level_desc")
		val levelDesc: String = ""
)
